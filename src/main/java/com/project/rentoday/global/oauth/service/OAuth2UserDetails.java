package com.project.rentoday.global.oauth.service;

import com.project.rentoday.global.oauth.dto.OauthUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class OAuth2UserDetails implements OAuth2User {

    private final OauthUserDto OauthUserDTO;

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    //권한 획득
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return OauthUserDTO.getRole();
            }
        });
        return collection;
    }

    @Override
    public String getName() {
        return OauthUserDTO.getName();
    }

    public String getUsername() {
        return OauthUserDTO.getUsername();
    }
}
