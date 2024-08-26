package com.project.rentoday.global.exception;


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
