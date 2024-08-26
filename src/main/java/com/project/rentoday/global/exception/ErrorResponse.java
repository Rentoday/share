package com.project.rentoday.global.exception;

import lombok.Getter;

import java.sql.Timestamp;


public class ErrorResponse {

    private String message;
    private int errorCode;
    private long timestamp;

    public ErrorResponse(String message, int errorCode) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = System.currentTimeMillis();
    }
}
