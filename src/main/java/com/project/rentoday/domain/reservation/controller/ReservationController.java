package com.project.rentoday.domain.reservation.controller;

import com.project.rentoday.domain.reservation.dto.CreateReservationRequestDto;
import com.project.rentoday.domain.reservation.dto.CreateReservationResponseDto;
import com.project.rentoday.domain.reservation.dto.ReadReservationAllResponseDto;
import com.project.rentoday.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    //예약 생성
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateReservationResponseDto> insertReservation(@RequestBody CreateReservationRequestDto requestDto) {
            CreateReservationResponseDto reservation = reservationService.createReservation(
                    requestDto.getParkId(),
                    requestDto.getMemberId(),
                    requestDto.getPaymentId(),
                    requestDto.getCheckIn(),
                    requestDto.getReservationUid(),
                    requestDto.getReservationName());
            return ResponseEntity.created(URI.create("/api/reservation/" + reservation.getId())).body(reservation);
    }

    //예약 삭제
    @DeleteMapping(value = "/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
            reservationService.cancelReservation(id);
            return ResponseEntity.noContent().build();
    }

    // 사용자별 예약 조회
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ReadReservationAllResponseDto> readAllReservationByMember(@PathVariable Long memberId) {
        ReadReservationAllResponseDto memberInfo = reservationService.findMemberCheckIn(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(memberInfo);
    }
}
