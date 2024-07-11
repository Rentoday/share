package com.project.rentoday.global.jwt.exception;

import lombok.Getter;

@Getter
public class JwtException extends RuntimeException{

    private final JwtErrorCode jwtErrorCode;

    public JwtException(JwtErrorCode jwtErrorCode) {
        super(jwtErrorCode.getMessage());
        this.jwtErrorCode = jwtErrorCode;
    }
}
