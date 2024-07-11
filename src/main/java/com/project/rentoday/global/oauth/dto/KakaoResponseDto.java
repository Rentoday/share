package com.project.rentoday.global.oauth.dto;

import lombok.RequiredArgsConstructor;

import java.util.Map;

/*
카카오의 키는 "kakao_account"
각각의 요소는 id, nickname, email 로 진행
*/
@RequiredArgsConstructor
public class KakaoResponseDto implements OAuth2Response {

    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        return (String) ((Map)attribute.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map)attribute.get("properties")).get("nickname");
    }

    @Override
    public String getProfileImage() {
        return (String) ((Map)attribute.get("properties")).get("profile_image");
    }
}
