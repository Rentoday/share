package com.project.rentoday.global.oauth.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.global.jwt.entity.RefreshToken;
import com.project.rentoday.global.jwt.repository.RefreshRepository;
import com.project.rentoday.global.jwt.service.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class Oauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2UserDetails customUserDetails = (OAuth2UserDetails)authentication.getPrincipal();

        //id추출
        String username = customUserDetails.getUsername();
        System.out.println(username);

        //권한추출
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //토큰 생성
        String accessToken = jwtService.createAccessJwt(username, role);
        String refreshToken = jwtService.createRefreshJwt();

        //Refresh Token DB에 저장
        saveRefreshToken(username, refreshToken);

        //쿠키에 JWT 담아서 리다이렉트(리다이렉트이기때문에 header로 응답 불가능)
        response.addCookie(createCookie("Authorization", accessToken));
        response.addCookie(createCookie("Refresh", refreshToken));
        response.sendRedirect("http://localhost:81/main");

    }

    //쿠키생성 메서드
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*60);
        //cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    //db에 refreshToken 저장
    private void saveRefreshToken(String email, String refreshToken) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Date date = new Date(System.currentTimeMillis() + 86400000L);
        RefreshToken refresh = new RefreshToken(refreshToken, member, date.toString());
        refreshRepository.save(refresh);
    }
}
