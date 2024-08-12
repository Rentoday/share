package com.project.rentoday.domain.member.exception;

public class MemberNotFoundException extends IllegalArgumentException {

    public MemberNotFoundException(String message) {
        super(message);
    }
}
