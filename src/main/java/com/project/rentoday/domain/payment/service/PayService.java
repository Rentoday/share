package com.project.rentoday.domain.payment.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.payment.dto.request.PayCallbackRequestDto;
import com.project.rentoday.domain.payment.dto.request.PayRequestDto;
import com.project.rentoday.domain.payment.dto.response.PayInfoResponse;
import com.project.rentoday.domain.payment.entity.Pay;
import com.project.rentoday.domain.payment.entity.PaymentStatus;
import com.project.rentoday.domain.payment.repository.PayRepository;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.repository.ReservationRepository;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class PayService {

    private final ReservationRepository reservationRepository;
    private final PayRepository payRepository;
    private final MemberRepository memberRepository;
    private final IamportClient iamportClient;

    public PayRequestDto requestPay(String reservationUid) {

        Reservation reservation = reservationRepository.findReservationAndPayAndMember(reservationUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문이 없습니다."));

        return PayRequestDto.builder()
                .buyerName(reservation.getMember().getName())
                .buyerEmail(reservation.getMember().getEmail())
                .price(reservation.getPay().getAmount())
                .reservationName(reservation.getReservationName())
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

    public void payByCallback(PayCallbackRequestDto requestDto) {

        try {
            IamportResponse<Payment> iamportResponse = iamportClient.paymentByImpUid(requestDto.getPaymentUid());

            Reservation reservation = reservationRepository.findReservationAndPay(requestDto.getReservationUid())
                    .orElseThrow(() -> new IllegalArgumentException("예약 내역이 없습니다."));

            validatePayment(iamportResponse, reservation);

            updatePaymentStatus(reservation, iamportResponse);

        } catch (IamportResponseException e) {
            throw new RuntimeException("Iamport 응답 처리 중 오류가 발생했습니다.", e);
        } catch (IOException e) {
            throw new RuntimeException("네트워크 통신 중 오류가 발생했습니다.", e);
        }
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

    private void updatePaymentStatus(Reservation reservation, IamportResponse<Payment> iamportResponse) {
        reservation.getPay().changePayBySuccess(PaymentStatus.OK, iamportResponse.getResponse().getImpUid());
        payRepository.save(reservation.getPay());
    }

    public void someMethod(String reservationUid) {
        Reservation reservation = reservationRepository.findReservationAndPay(reservationUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약이 존재하지 않습니다."));

        Pay pay = reservation.getPay();


    }
}
