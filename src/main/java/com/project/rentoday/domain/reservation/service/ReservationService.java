package com.project.rentoday.domain.reservation.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkImage;
import com.project.rentoday.domain.park.repository.ParkImageRepository;
import com.project.rentoday.domain.park.repository.ParkRepository;
import com.project.rentoday.domain.payment.exception.ResourceNotFoundException;
import com.project.rentoday.domain.reservation.dto.CreateReservationResponseDto;
import com.project.rentoday.domain.reservation.dto.ReadReservationAllResponseDto;
import com.project.rentoday.domain.reservation.dto.ReservationDetailsDto;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.exception.ReservationAlreadyExistsException;
import com.project.rentoday.domain.reservation.exception.ReservationNotAvailableException;
import com.project.rentoday.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ParkRepository parkRepository;
    private final ParkImageRepository parkImageRepository;

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new IllegalArgumentException("해당 예약 번호를 찾을 수 없습니다."));

        reservation.cancel();

        reservationRepository.save(reservation);
    }

    @Transactional
    public CreateReservationResponseDto createReservation(Long parkId, Long memberId, LocalTime checkIn, String reservationUid, String reservationName) {
        Park park = parkRepository.findById(parkId).orElseThrow(() -> new IllegalArgumentException("해당 주차장을 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        if (!isAvailableCheckInOut(park, checkIn)) {
            throw new ReservationNotAvailableException("선택한 예약 시간은 이용하실 수 없습니다.");
        }

//        if (!isDuplicatedCheckIn(park, checkIn)) {
//            throw new ReservationAlreadyExistsException("이미 예약된 시간입니다.");
//        }

        Reservation reservation = new Reservation(member, park, null, checkIn, reservationUid, reservationName);
        Reservation savedReservation = reservationRepository.save(reservation);

        return new CreateReservationResponseDto(savedReservation);
    }

    public ReservationDetailsDto getReservationDetailsByUid(String uid) {
        Reservation reservation = reservationRepository.findByReservationUid(uid)
                .orElseThrow(() -> new EntityNotFoundException("해당 예약을 찾을 수 없습니다: " + uid));

        Park park = reservation.getPark();
        Member member = reservation.getMember();

        // 주차장 이미지 URL 가져오기 (첫 번째 이미지만 사용)
        String imageUrl = parkImageRepository.findFirstByParkId(park.getId())
                .map(ParkImage::getParkingImageUrl)
                .orElse(null);

        // 예약 시간 계산
        long durationHours = ChronoUnit.HOURS.between(reservation.getCheckIn(), reservation.getCheckOut());
        double totalPrice = durationHours * park.getPrice();

        return ReservationDetailsDto.builder()
                .reservationUid(reservation.getReservationUid())
                .reservationName(reservation.getReservationName())
                .parkingNum(park.getParkingNum())
                .startTime(formatDateTime(reservation.getCheckIn()))
                .endTime(formatDateTime(reservation.getCheckOut()))
                .price(park.getPrice())
                .agency(park.getAgency())
                .agencyPhone(park.getAgNum())
                .description(park.getContent())
                .imageUrl(imageUrl)
                .duration((int) durationHours)
                .totalPrice(totalPrice)
                .buyerEmail(member.getEmail())
                .buyerName(member.getName())
                .build();
    }
    @Transactional(readOnly = true)
    public Page<ReadReservationAllResponseDto> findReservationByMember(String email, int page, int size) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Reservation> reservationsPage = reservationRepository.findByMember(member, pageable);

        List<ReadReservationAllResponseDto> dtoList = List.of(new ReadReservationAllResponseDto(reservationsPage.getContent()));

        return new PageImpl<>(dtoList, pageable, reservationsPage.getTotalElements());
    }

    private String formatDateTime(@NotNull @FutureOrPresent LocalTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    //체크인,아웃 시간이 판매 가능 시작,끝 시간 안에 있는지
    private boolean isAvailableCheckInOut(Park park, LocalTime checkIn) {
        return !checkIn.isBefore(park.getStartTime()) && !checkIn.isAfter(park.getEndTime());
    }

//    //사용자들끼리의 체크인 시간이 겹치지 않는지
//    private boolean isDuplicatedCheckIn(Park park, LocalTime checkIn) {
//        LocalTime checkOut = checkIn.plusMinutes(59);
//        List<Reservation> overlappingReservations = reservationRepository.findByParkAndCheckInBetween(park, checkIn, checkOut);
//        return !overlappingReservations.isEmpty();
//    }

    private Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("해당 번호로 예약을 찾을 수 없습니다."));
    }

}
