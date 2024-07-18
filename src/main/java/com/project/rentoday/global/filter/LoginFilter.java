package com.project.rentoday.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.global.jwt.entity.RefreshToken;
import com.project.rentoday.global.jwt.repository.RefreshRepository;
import com.project.rentoday.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;

//UsernamePasswordAuthenticationFilter를 커스텀하여 활성화 시킴
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    //검증을 위한 주입
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    @Override
    public void setFilterProcessesUrl(String filterUrl) {
        super.setFilterProcessesUrl("/api/member/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트의 json 요청에 대한 id, password 추출
        MemberDto.LoginRequest loginRequestDto;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ServletInputStream inputStream = request.getInputStream();
            String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            loginRequestDto = objectMapper.readValue(messageBody, MemberDto.LoginRequest.class);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //클라이언트에서 로그인시 요청보낸 id, password를 추출
        String username = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        System.out.println(username);
        System.out.println(password);

        //security에서 username과 password를 검증하기 위해서 token에 사용자 정보를 담아야한다.
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);

        //authenticationManager를 통해서 사용자의 id와 password가 담긴 token을 검증한다.
        return authenticationManager.authenticate(authToken);
    }

    //검증(로그인) 성공시
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        //authentication을 통해서 로그인한 유저id를 추출
        String email = authentication.getName();

        //authentication을 통해서 권한 추출 Collection으로 반환하기 때문에
        //Collection으로 받고 iterator를 통해 값을 권한을 추출한다.
        //CustomUserDetails의 getAuthorities() 메서드 참고
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        //추출한 id와 권한을 통해서 Access / Refresh Token 생성
        String accessToken = jwtService.createAccessJwt(email, role);
        String refreshToken = jwtService.createRefreshJwt();
        System.out.println(refreshToken);

        //refresh Token db에 저장
        saveRefreshToken(email, refreshToken);


        //jwt를 헤더를 통해 응답
        //HTTP 인증 방식은 RFC7235정의에 따라서 아래 인증 헤더 형태를 가져야한다. Bearer 접두사가 필수다
        response.addHeader("Authorization", "Bearer " + accessToken);
        response.addCookie(createCookie("Refresh", refreshToken));
        System.out.println(refreshToken + "발급");
        System.out.println(refreshToken + "발급");
        System.out.println(refreshToken + "발급");
        System.out.println(refreshToken + "발급");
        System.out.println(refreshToken + "발급");
        response.setStatus(HttpStatus.OK.value());
    }

    //검증 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR);
    }

    //db에 refreshToken 저장
    private void saveRefreshToken(String email, String refreshToken) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        String expiration = jwtService.expiredDate(refreshToken).toString();
        RefreshToken refresh = new RefreshToken(refreshToken, member, expiration);
        refreshRepository.save(refresh);
    }

    //cookie 저장 메서드
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        //쿠키의 생명주기
        cookie.setMaxAge(60*60*60);
//        cookie.setSecure(true);
       cookie.setPath("/");
        //js의 접근을 막는다.
        cookie.setHttpOnly(false);

        return cookie;
    }
}
