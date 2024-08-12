package com.project.rentoday.domain.reservation.exception;

public class ReservationAlreadyExistsException extends IllegalArgumentException {

    public ReservationAlreadyExistsException(String message) {
        super(message);
    }

}
