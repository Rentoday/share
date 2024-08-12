package com.project.rentoday.domain.reservation.exception;

public class ReservationNotFoundException extends IllegalArgumentException {

    public ReservationNotFoundException(String message) {
        super(message);
    }
}
