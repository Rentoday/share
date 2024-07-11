package com.project.rentoday.domain.member.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum EmailErrorCode {

    EMAIL_ENCODING_ERROR(HttpStatus.BAD_REQUEST, "EML-01", "email 전송에 encoding이 잘못되었습니다.", "api/verification/{id}"),
    EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "EML-02", "서버 오류로 인해 email 전송에 실패했습니다.", "api/verification/{id}");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String path;
}