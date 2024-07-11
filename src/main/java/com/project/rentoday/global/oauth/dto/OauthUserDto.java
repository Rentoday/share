package com.project.rentoday.global.oauth.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OauthUserDto {
    private String role;
    private String name;
    private String username;
}
