package com.project.rentoday.global.oauth.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.global.oauth.dto.KakaoResponseDto;
import com.project.rentoday.global.oauth.dto.OAuth2Response;
import com.project.rentoday.global.oauth.dto.OauthUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    //유저의 정보를 받아옴
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User);

        //registrationId : 소셜, 카카오 등
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponseDto(oAuth2User.getAttributes());
        } else {

            return  null;
        }

        String username = oAuth2Response.getProvider()+ " " + oAuth2Response.getProviderId();

        System.out.println(username);
        System.out.println(oAuth2Response.getName());
        System.out.println(oAuth2Response.getEmail());
        System.out.println(oAuth2Response.getProfileImage());

        Member existData = memberRepository.findByEmail(oAuth2Response.getEmail()).orElse(null);
        if (existData == null) {

            Member memberEntity = Member.createMember()
                    .email(oAuth2Response.getEmail())
                    .name(oAuth2Response.getName())
                    .profileImage(oAuth2Response.getProfileImage())
                    .build();
            memberRepository.save(memberEntity);

            OauthUserDto userDTO = new OauthUserDto();
            userDTO.setUsername(oAuth2Response.getEmail());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("ROLE_USER");

            return new OAuth2UserDetails(userDTO);

        }else {
            existData.updateProfile("욘트리", oAuth2Response.getProfileImage());
            memberRepository.save(existData);

            OauthUserDto userDTO = new OauthUserDto();
            userDTO.setUsername(existData.getEmail());
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole(existData.getRoleType().getName());

            return new OAuth2UserDetails(userDTO);
        }

    }


}
