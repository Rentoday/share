package com.project.rentoday.global.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtDto {
    private String accessToken;
    private String refreshToken;
}
