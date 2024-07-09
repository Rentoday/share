package com.project.rentoday.domain.reservation.exception;

public class ReservationAlreadyExistsException extends RuntimeException {

    public ReservationAlreadyExistsException(String message) {
        super(message);
    }

    public ReservationAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
