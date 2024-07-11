package com.project.rentoday.global.file.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum FileErrorCode {

    FILE_MAX_SIZE_ERROR(HttpStatus.BAD_REQUEST, "FL-01", "프로필 이미지 사이즈를 초과하였습니다. 10MB이하로 업로드해주세요.", "/api/join"),
    FILE_EXTENSION_ERROR(HttpStatus.BAD_REQUEST, "FL-02", "업로드파일 확장자명을 확인해주세요.", "/api/join");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    private final String path;
}
