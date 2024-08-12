package com.project.rentoday.domain.payment.exception;

public class PayNotFoundException extends IllegalArgumentException {
    public PayNotFoundException(String message) {
        super(message);
    }
}
