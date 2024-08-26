package com.project.rentoday.domain.member.exception;

import lombok.Getter;

@Getter
public class EmailException extends RuntimeException{

    private final EmailErrorCode emailErrorCode;

    public EmailException(EmailErrorCode emailErrorCode) {
        super(emailErrorCode.getMessage());
        this.emailErrorCode = emailErrorCode;
    }
}
