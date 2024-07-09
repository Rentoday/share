package com.project.rentoday.domain.member.exception;

import com.project.rentoday.global.exception.ErrorCode;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MemberNotFoundException extends RuntimeException {

    private final ErrorCode errorCode;

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
