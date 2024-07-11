package com.project.rentoday.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {

    MEMBER_DUPLICATE_EMAIL_ERROR(HttpStatus.CONFLICT, "MEM-01", "이미 존재하는 email입니다."),
    MEMBER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "MEM-02", "존재하지 않는 회원입니다."),
    MEMBER_INVALID_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "MEM-03", "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
