package com.project.rentoday.domain.reservation.dto;

import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.reservation.entity.Reservation;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ReadReservationResponseDto {

    private Long reservationId;

    private String parkNum;

    private String address;

    private LocalTime checkIn;

    private LocalTime checkOut;

    private double price;

    private LocalDateTime regDate;

    public ReadReservationResponseDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.parkNum = reservation.getPark().getParkingNum();
        this.address = reservation.getPark().getAddress();
        this.checkIn = reservation.getCheckIn();
        this.checkOut = reservation.getCheckOut();
        this.price = reservation.getAmount();
        this.regDate = reservation.getCreatedDate();
    }

}
