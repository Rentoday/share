package com.project.rentoday.domain.notice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NoticeErrorCode {

    NOTICE_NOT_FOUND_ERROR(HttpStatus.CONFLICT, "COMM-01", "존재하지 않는 공지입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
