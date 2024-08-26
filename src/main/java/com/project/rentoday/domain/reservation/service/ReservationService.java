package com.project.rentoday.domain.reservation.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.entity.ParkImage;
import com.project.rentoday.domain.park.exception.ParkIdNotFoundException;
import com.project.rentoday.domain.park.repository.ParkImageRepository;
import com.project.rentoday.domain.park.repository.ParkRepository;
import com.project.rentoday.domain.reservation.dto.CreateReservationRequestDto;
import com.project.rentoday.domain.reservation.dto.ReadReservationAllResponseDto;
import com.project.rentoday.domain.reservation.dto.ReservationDetailsDto;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.exception.ReservationAlreadyExistsException;
import com.project.rentoday.domain.reservation.exception.ReservationNotAvailableException;
import com.project.rentoday.domain.reservation.exception.ReservationNotFoundException;
import com.project.rentoday.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final Logger log = LoggerFactory.getLogger(ReservationService.class);
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
    public List<Reservation> makeReservations(CreateReservationRequestDto requestDto) {
        Park park = parkRepository.findById(requestDto.getParkId())
                .orElseThrow(() -> new ParkIdNotFoundException("해당 주차 공간을 찾을 수 없습니다."));

        Member member = memberRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        List<LocalTime> sortedCheckInTimes = requestDto.getCheckInTimes().stream()
                .sorted()
                .collect(Collectors.toList());

        List<Reservation> reservations = new ArrayList<>();
        double pricePerReservation = requestDto.getEstimatedPrice() / sortedCheckInTimes.size();

        for (LocalTime checkIn : sortedCheckInTimes) {
            LocalTime checkOut = checkIn.plusHours(1);

            // 운영 시간 내인지 확인
            if (checkIn.isBefore(park.getStartTime()) || checkOut.isAfter(park.getEndTime())) {
                throw new ReservationNotAvailableException("해당 예약은 이용하실 수 없습니다.");
            }

            // 베타 락을 통해 이미 예약 중인지 확인
            List<Reservation> startedReservations = reservationRepository
                    .findExistedReservationsWithLock(park, checkIn, checkOut);

            if (!startedReservations.isEmpty()) {
                throw new ReservationAlreadyExistsException("해당 시간은 이미 예약되었습니다.");
            }

            // 새로운 예약 생성
            Reservation reservation = new Reservation(member, park, checkIn, pricePerReservation, checkOut);
            reservations.add(reservationRepository.save(reservation));
        }

        return reservations;
    }

    @Transactional(readOnly = true)
    public ReservationDetailsDto getReservationDetails(String reservationUid) {
        Reservation reservation = reservationRepository.findByReservationUid(reservationUid)
                .orElseThrow(() -> new ReservationNotFoundException("해당 예약을 찾을 수 없습니다."));

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

}
