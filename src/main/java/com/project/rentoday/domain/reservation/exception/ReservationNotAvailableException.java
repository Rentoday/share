package com.project.rentoday.domain.reservation.exception;

import com.project.rentoday.global.exception.ErrorCode;

public class ReservationNotAvailableException extends RuntimeException {

    public ReservationNotAvailableException(ErrorCode message) {
        super(message);
    }

    public ReservationNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
