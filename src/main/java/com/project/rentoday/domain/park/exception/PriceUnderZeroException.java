package com.project.rentoday.domain.park.exception;


public class PriceUnderZeroException extends RuntimeException {

    public PriceUnderZeroException(String message) {
        super(message);
    }
}
