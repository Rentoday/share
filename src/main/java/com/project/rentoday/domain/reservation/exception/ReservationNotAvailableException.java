package com.project.rentoday.domain.reservation.exception;

public class ReservationNotAvailableException extends RuntimeException {

    public ReservationNotAvailableException(String message) {
        super(message);
    }

    public ReservationNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
