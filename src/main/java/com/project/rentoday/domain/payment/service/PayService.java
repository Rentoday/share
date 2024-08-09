package com.project.rentoday.domain.payment.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.service.MessageService;
import com.project.rentoday.domain.notification.service.RedisMessagePublisher;
import com.project.rentoday.domain.payment.dto.request.PayCallbackRequestDto;
import com.project.rentoday.domain.payment.dto.request.PayRequestDto;
import com.project.rentoday.domain.payment.dto.response.PayInfoResponse;
import com.project.rentoday.domain.payment.entity.Pay;
import com.project.rentoday.domain.payment.entity.PaymentStatus;
import com.project.rentoday.domain.payment.repository.PayRepository;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.entity.ReservationStatus;
import com.project.rentoday.domain.reservation.repository.ReservationRepository;
import com.project.rentoday.global.type.NotificationType;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PayService {

    private final ReservationRepository reservationRepository;
    private final PayRepository payRepository;
    private final MemberRepository memberRepository;
    private final IamportClient iamportClient;
    private final MessageService messageService;
    private final ApplicationEventPublisher publisher;
    private final RedisMessagePublisher redisMessagePublisher;

    private static final int MAX_RETRY_COUNT = 3;  // 최대 3번 재시도
    private static final long RETRY_DELAY = 2000L; // 2초(2000 밀리초) 대기

    public PayRequestDto requestPay(String reservationUid) {
        Reservation reservation = reservationRepository.findReservationAndPayAndMember(reservationUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));

        return PayRequestDto.builder()
                .buyerName(reservation.getMember().getName())
                .buyerEmail(reservation.getMember().getEmail())
                .price(reservation.getAmount())
                .reservationUid(reservation.getReservationUid())
                .build();
    }

    @Transactional(readOnly = true)
    public Page<PayInfoResponse> getPaymentsByMember(String email, int page, int size) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Reservation> reservations = reservationRepository.findByMemberAndPayIsNotNull(member, pageable);

        return reservations.map(reservation -> new PayInfoResponse(reservation.getPay().getId(), reservation.getPay()));
    }

    @Transactional
    public void payByCallback(PayCallbackRequestDto requestDto) {
        try {
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(requestDto.getPaymentUid());
            Payment payment = iamportResponse.getResponse();

            Reservation reservation = reservationRepository.findReservationAndPay(requestDto.getReservationUid())
                    .orElseThrow(() -> new IllegalArgumentException("예약 내역이 없습니다."));

            //결제 금액 검증
            if (!payment.getAmount().equals(reservation.getAmount())) {
                throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
            }
            //결제 상태 검증
            if (!"paid".equals(payment.getStatus())) {
                throw new IllegalStateException("결제가 완료되지 않았습니다.");
            }

            validatePayment(iamportResponse, reservation);
            Pay savedPay = savePaymentInfo(reservation, payment);
            updatePaymentStatus(reservation, savedPay);
            sendPaymentCompletionNotification(reservation);
        } catch (Exception e) {
            log.error("결제 처리 중 오류가 발생했습니다.");
            try {
                cancelPaymentByImpUid(requestDto.getPaymentUid());
            } catch (Exception cancelException) {
                log.error("결제 취소 중 오류가 발생했습니다.", cancelException);
            }
            throw new RuntimeException("결제 처리 중 오류가 발생했습니다." + e.getMessage(), e);
        }

       }

    @Transactional
    public void cancelPaymentByImpUid(String impUid) {
        try {
            // Iamport에서 결제 정보 조회
            IamportResponse<Payment> paymentResponse = iamportClient.paymentByImpUid(impUid);
            Payment iamportPayment = paymentResponse.getResponse();

            // 결제 정보로 Pay 엔티티 조회
            Pay pay = payRepository.findByImpUid(impUid)
                    .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역이 없습니다."));

            // 환불 요청
            CancelData cancelData = new CancelData(iamportPayment.getImpUid(), true, BigDecimal.valueOf(pay.getAmount()));
            IamportResponse<Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);

            // 환불 상태 확인 및 처리
            if (cancelResponse.getResponse().getStatus().equals("cancelled")) {
                pay.changePayByCancel(PaymentStatus.CANCELLED);
                payRepository.save(pay);

                Reservation reservation = pay.getReservation();
                reservation.setStatus(ReservationStatus.CANCEL);
                reservationRepository.save(reservation);

                // 결제 취소 알림 발송
                sendPaymentCancellationNotification(reservation);
            } else {
                throw new RuntimeException("결제 취소에 실패했습니다.");
            }
        } catch (IamportResponseException | IOException e) {
            log.error("결제 취소 중 오류가 발생했습니다.", e);

            // 환불 실패 시 자동 재시도 로직 실행
            retryCancelPayment(impUid);
        }
    }

    private void retryCancelPayment(String impUid) {
        int retryCount = 0; // 재시도 횟수
        boolean success = false; // 기본값

        while (retryCount < MAX_RETRY_COUNT && !success) {

            try {
                //일정 시간 대기 후 재시도
                Thread.sleep(RETRY_DELAY);
                IamportResponse<Payment> paymentIamportResponse = iamportClient.paymentByImpUid(impUid);
                Payment iamportPayment = paymentIamportResponse.getResponse();

                //환불 요청 재시도
                CancelData cancelData = new CancelData(
                        iamportPayment.getImpUid(),
                        true,
                        BigDecimal.valueOf(iamportPayment.getAmount().doubleValue()));
                IamportResponse<Payment> cancelResponse = iamportClient.cancelPaymentByImpUid(cancelData);

                if (cancelResponse.getResponse().getStatus().equals("cancelled")) {
                    success = true;
                    //결제 취소 성공 처리
                    handleSuccessfulCancellation(iamportPayment);
                }
            } catch (IamportResponseException | IOException | InterruptedException exception) {
                log.error("환불 재시도 중 오류가 발생했습니다.", exception);
                retryCount++;
            }
        }
        if (!success) {
            sendErrorNotification("환불 실패", new RuntimeException("최대 재시도 횟수를 초과했습니다."));
        }
    }

    private void handleSuccessfulCancellation(Payment iamportPayment) {
        //환불 성공 시 DB 업데이트
        Pay pay = payRepository.findByImpUid(iamportPayment.getImpUid())
                .orElseThrow(() -> new IllegalArgumentException("해당 결제 내역이 없습니다."));
        pay.changePayByCancel(PaymentStatus.CANCELLED);
        payRepository.save(pay);

        Reservation reservation = pay.getReservation();
        reservation.setStatus(ReservationStatus.CANCEL);
        reservationRepository.save(reservation);

        //사용자에게 결제 취소 및 환불 성공 알림 전송
        sendPaymentCancellationNotification(reservation);
    }

    private Pay savePaymentInfo(Reservation reservation, Payment payment) {
        Pay pay = new Pay();
        pay.setReservation(reservation);
        pay.setAmount(payment.getAmount().doubleValue());
        pay.setStatus(PaymentStatus.OK);
        pay.setPaymentUid(payment.getImpUid());
        pay.setImpUid(payment.getImpUid());

        Pay savedPay = payRepository.save(pay);

        // 결제 완료 후 알림 메시지 생성 및 발송
        NotificationDto.CreateRequest message = new NotificationDto.CreateRequest(
                messageService.commentMessage(pay.getReservation().getMember().getName()),
                pay.getReservation().getPark().getMember().getEmail(),
                NotificationType.PAYMENT);
        redisMessagePublisher.publishTopic(pay.getReservation().getPark().getMember().getEmail(), message);

        return savedPay;
    }

    private void validatePayment(IamportResponse<Payment> iamportResponse, Reservation reservation) {
        if (!iamportResponse.getResponse().getStatus().equals("paid")) {
            cancelReservationAndPayment(reservation);
            throw new RuntimeException("결제가 완료되지 않았습니다.");
        }

        double expectedPrice = reservation.getPay().getAmount();
        int actualPrice = iamportResponse.getResponse().getAmount().intValue();

        if (actualPrice != expectedPrice) {
            cancelReservationAndPayment(reservation);
            cancelIamportPayment(iamportResponse.getResponse());
            throw new RuntimeException("결제금액 불일치");
        }
    }

    private void cancelReservationAndPayment(Reservation reservation) {
        reservationRepository.delete(reservation);
        payRepository.delete(reservation.getPay());
    }

    private void cancelIamportPayment(Payment payment) {
        try {
            BigDecimal cancelAmount = BigDecimal.valueOf(payment.getAmount().doubleValue());
            iamportClient.cancelPaymentByImpUid(new CancelData(payment.getImpUid(), true, cancelAmount));
        } catch (IamportResponseException e) {
            throw new RuntimeException("포트원 결제 취소 중 Iamport 응답 오류가 발생했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException("포트원 결제 취소 중 네트워크 오류가 발생했습니다.", e);
        }
    }

    private void updatePaymentStatus(Reservation reservation, Pay iamportResponse) {
        reservation.getPay().changePayBySuccess(PaymentStatus.OK, iamportResponse.getImpUid());
        payRepository.save(reservation.getPay());
    }

    private void sendPaymentCompletionNotification(Reservation reservation) {
        String message = messageService.salesMessage(reservation.getMember().getEmail());
        NotificationDto.CreateRequest notificationRequest = new NotificationDto.CreateRequest(
                message,
                reservation.getPark().getMember().getEmail(),
                NotificationType.PAYMENT
        );
        publisher.publishEvent(notificationRequest);
    }

    private void sendPaymentCancellationNotification(Reservation reservation) {
        String message = messageService.salesCancelMessage(reservation.getMember().getEmail());
        NotificationDto.CreateRequest notificationRequest = new NotificationDto.CreateRequest(
                message,
                reservation.getPark().getMember().getEmail(),
                NotificationType.PAYMENT_CANCEL
        );
        publisher.publishEvent(notificationRequest);
    }

}
