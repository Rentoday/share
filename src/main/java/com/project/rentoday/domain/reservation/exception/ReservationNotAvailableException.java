package com.project.rentoday.domain.reservation.exception;


public class ReservationNotAvailableException extends IllegalArgumentException {

    public ReservationNotAvailableException(String message) {
        super(message);
    }
}
