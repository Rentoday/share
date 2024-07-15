package com.project.rentoday.domain.notice.exception;

import lombok.Getter;

@Getter
public class NoticeException extends RuntimeException{

    private final NoticeErrorCode noticeErrorCode;

    public NoticeException(NoticeErrorCode noticeErrorCode) {
        super(noticeErrorCode.getMessage());
        this.noticeErrorCode = noticeErrorCode;
    }
}
