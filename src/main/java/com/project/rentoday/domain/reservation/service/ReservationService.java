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
import com.project.rentoday.domain.reservation.dto.CreateReservationRequestDto;
import com.project.rentoday.domain.reservation.dto.ReadReservationAllResponseDto;
import com.project.rentoday.domain.reservation.dto.ReservationDetailsDto;
import com.project.rentoday.domain.reservation.entity.Reservation;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    public Reservation createReservation(CreateReservationRequestDto requestDto) {
        Park park = parkRepository.findById(requestDto.getParkId())
                .orElseThrow(() -> new IllegalArgumentException("해당 주차장을 찾을 수 없습니다."));
        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        LocalTime latestCheckIn = requestDto.getCheckInTimes().stream()
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("체크인 시간이 비어 있습니다."));
        LocalTime earliestCheckIn = requestDto.getCheckInTimes().stream()
                .min(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalArgumentException("체크인 시간이 비어있습니다."));

        LocalTime checkOutTime = latestCheckIn.plusHours(1);

        requestDto.setCheckOutTime(checkOutTime);
        System.out.println("checkOutTime = " + checkOutTime);

        Reservation savedReservation = null;
        for (LocalTime checkIn : requestDto.getCheckInTimes()) {
            Reservation reservation = new Reservation(member, park, earliestCheckIn, requestDto.getEstimatedPrice(), checkOutTime);
            savedReservation = reservationRepository.save(reservation);
        }

        return savedReservation;
    }

    @Transactional(readOnly = true)
    public ReservationDetailsDto getReservationDetails(String reservationUid) {
        Reservation reservation = reservationRepository.findByReservationUid(reservationUid)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        Park park = reservation.getPark();
        Member member = reservation.getMember();

        return new ReservationDetailsDto(
                reservationUid,
                park.getParkingNum(),
                reservation.getCheckIn(),
                reservation.getCheckOut(),
                park.getPrice(),
                park.getAgency(),
                park.getAgNum(),
                park.getContent(),
                member.getEmail(),
                member.getName()
        );
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



        return ReservationDetailsDto.builder()
                .reservationUid(reservation.getReservationUid())
                .parkingNum(park.getParkingNum())
                .startTime((reservation.getCheckIn()))
                .endTime((reservation.getCheckOut()))
                .price(park.getPrice())
                .agency(park.getAgency())
                .agencyPhone(park.getAgNum())
                .description(park.getContent())
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

    private Reservation getReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("해당 번호로 예약을 찾을 수 없습니다."));
    }

}
