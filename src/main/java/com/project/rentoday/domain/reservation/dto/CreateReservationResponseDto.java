package com.project.rentoday.domain.reservation.dto;

import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.entity.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class CreateReservationResponseDto {

    //예약 아이디
    private Long id;

    private String reservationUid;

    private Long parkId;

    private Long memberId;

    private LocalTime checkIn;

    private LocalTime checkOut;


    private ReservationStatus reservationStatus;

    private double price;

    public CreateReservationResponseDto(Reservation reservation) {
        this.id = reservation.getId();
        this.reservationUid = reservation.getReservationUid();
        this.parkId = reservation.getPark().getId();
        this.memberId = reservation.getMember().getId();
        this.checkIn = reservation.getCheckIn();
        this.checkOut = reservation.getCheckOut();
        this.reservationStatus = reservation.getReservationStatus();
        this.price = reservation.getAmount();
    }
}
