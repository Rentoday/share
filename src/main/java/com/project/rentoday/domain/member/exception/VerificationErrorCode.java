package com.project.rentoday.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VerificationErrorCode {

    VERIFICATION_EXPIRATION_CODE_ERROR(HttpStatus.BAD_REQUEST, "VE-01", "인증코드가 만료되었거나, 올바른 이메일을 입력해주세요.", "api/emailCheck"),
    VERIFICATION_CODE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "VE-02", "인증코드가 일치하지 않습니다. 인증코드를 정확히 입력해주세요.", "api/emailCheck"),
    VERIFICATION_ERROR(HttpStatus.BAD_REQUEST, "VE-03", "인증버튼으로 이메일 인증을 완료해주세요.", "api/emailCheck");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String path;
}
