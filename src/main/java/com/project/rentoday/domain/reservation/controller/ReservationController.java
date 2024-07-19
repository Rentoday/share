package com.project.rentoday.domain.reservation.controller;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.reservation.dto.CreateReservationRequestDto;
import com.project.rentoday.domain.reservation.dto.CreateReservationResponseDto;
import com.project.rentoday.domain.reservation.dto.ReadReservationAllResponseDto;
import com.project.rentoday.domain.reservation.dto.ReservationDetailsDto;
import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.service.ReservationService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
@Api(tags = "Reservation")
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    //예약 생성
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateReservationResponseDto> insertReservation(
            @RequestBody CreateReservationRequestDto requestDto,
            @AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        requestDto.setEmail(email);

        Reservation reservation = reservationService.createReservation(requestDto);
        System.out.println("reservation = " + reservation);

        CreateReservationResponseDto responseDto = new CreateReservationResponseDto(reservation);
        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/details")
    public ResponseEntity<ReservationDetailsDto> getReservationDetails(@RequestParam("uid") String reservationUid) {
        ReservationDetailsDto details = reservationService.getReservationDetailsByUid(reservationUid);
        return ResponseEntity.ok(details);
    }
    //예약 삭제
    @DeleteMapping(value = "/{reservationId}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> cancelReservation(@PathVariable Long reservationId) {
            reservationService.cancelReservation(reservationId);
            return ResponseEntity.noContent().build();
    }

    // 사용자별 예약 조회
    @GetMapping(value = "/member", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ReadReservationAllResponseDto>> readAllReservationByMember(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = principal.getUsername();
        Page<ReadReservationAllResponseDto> response = reservationService.findReservationByMember(email, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
