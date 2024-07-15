package com.project.rentoday.domain.reservation.dto;

import com.project.rentoday.domain.reservation.entity.Reservation;
import com.project.rentoday.domain.reservation.entity.ReservationStatus;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
public class CreateReservationResponseDto {

    //예약 아이디
    private Long id;

    private String reservationUid;

    private Long parkId;

    private Long memberId;

    private LocalDateTime checkIn;

    private LocalDateTime checkOut;

    private Duration rentalDuration;

    private ReservationStatus reservationStatus;

    private double amount;

    private String reserveNumber;

    public CreateReservationResponseDto(Reservation reservation) {
        this.id = reservation.getId();
        this.reservationUid = reservation.getReservationUid();
        this.parkId = reservation.getPark().getId();
        this.memberId = reservation.getMember().getId();
        this.checkIn = reservation.getCheckIn();
        this.checkOut = reservation.getCheckOut();
        this.rentalDuration = reservation.getRentalDuration();
        this.reservationStatus = reservation.getReservationStatus();
        this.amount = reservation.getAmount();
        this.reserveNumber = reservation.getReservationUid();
    }
}
