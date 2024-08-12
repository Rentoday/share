package com.project.rentoday.domain.park.exception;


public class EndTimeBeforeStartTimeException extends IllegalArgumentException{

    public EndTimeBeforeStartTimeException(String message) {
        super(message);
    }
}
