package com.project.rentoday.domain.park.exception;

import com.project.rentoday.global.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParkIdNotFoundException extends RuntimeException {
    private final ErrorCode errorCode;

    @Override
    public String getMessage () {
        return errorCode.getMessage();
    }
}
