package com.project.rentoday.domain.member.exception;

import lombok.Getter;

@Getter
public class VerificationException extends RuntimeException{

    private final VerificationErrorCode verificationErrorCode;

    public VerificationException(VerificationErrorCode verificationErrorCode) {
        super(verificationErrorCode.getMessage());
        this.verificationErrorCode = verificationErrorCode;
    }
}