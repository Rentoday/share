package com.project.rentoday.global.jwt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorCode {

    JWT_OAUTH2_ACCESS_REISSUANCE(HttpStatus.UNAUTHORIZED, "JWT-01", "OAuth2 로그인으로 JWT 재발급을 진행해야합니다."),
    JWT_ACCESS_EXPIRATION_ERROR(HttpStatus.BAD_REQUEST, "JWT-02", "만료된 AccessToken입니다. 재발급해주세요."),
    JWT_REFRESH_NOT_FOUND_ERROR(HttpStatus.BAD_REQUEST, "JWT-03", "RefreshToken이 없습니다. 잘못된 사용자입니다."),
    JWT_REFRESH_EXPIRATION_ERROR(HttpStatus.BAD_REQUEST, "JWT-04", "만료된 RefreshToken입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
