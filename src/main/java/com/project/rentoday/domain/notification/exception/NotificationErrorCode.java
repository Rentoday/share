package com.project.rentoday.domain.notification.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode {

    NOTIFICATION_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "NOT-01", "알림이 없습니다."),
    MEMBER_INVALID_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "MEM-03", "비밀번호가 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
