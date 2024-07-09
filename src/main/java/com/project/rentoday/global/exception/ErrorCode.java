package com.project.rentoday.global.exception;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public enum ErrorCode {

    INVALID_PARK_ID(BAD_REQUEST, "존재하지 않는 주차입니다."),
    INVALID_MEMBER(BAD_REQUEST, "존재하지 않는 회원입니다."),
    INVALID_PARK_NUM(BAD_REQUEST, "유효하지 않은 구획번호입니다."),
    INVALID_END_TIME(BAD_REQUEST, "종료 시간은 시작 시간보다 이후여야 합니다."),
    INVALID_PRICE(BAD_REQUEST, "가격은 1000원보다 커야 합니다."),
    ;

    private final HttpStatus status;
    private final String message;

    ErrorCode(final HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
