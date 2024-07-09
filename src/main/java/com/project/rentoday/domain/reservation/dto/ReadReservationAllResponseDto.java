package com.project.rentoday.domain.reservation.dto;

import com.project.rentoday.domain.reservation.entity.Reservation;

import java.util.List;
import java.util.stream.Collectors;

public class ReadReservationAllResponseDto {

    private List<ReadReservationResponseDto> reservations;

    public ReadReservationAllResponseDto(List<Reservation> reservations) {
        this.reservations = reservations.stream()
                .map(ReadReservationResponseDto::new)
                .collect(Collectors.toList());
    }
}
