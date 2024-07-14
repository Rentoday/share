package com.project.rentoday.global.filter;

import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.member.sevice.CustomMemberDetails;
import com.project.rentoday.global.jwt.exception.JwtErrorCode;
import com.project.rentoday.global.jwt.exception.JwtException;
import com.project.rentoday.global.jwt.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//요청에 의해 딱 한번만 실행되는 필터
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //HttpHeader속 Access Token 추출
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            //OAuth2 로그인 검증
            filterChain.doFilter(request, response);
            return;
        }

        //Bearer 부분 제거 후 순수 AccessToken만 획득
        String accessToken = authorization.split(" ")[1];

        //토큰 소멸시간 검증
        if (jwtService.isExpired(accessToken)) {

            throw new JwtException(JwtErrorCode.JWT_ACCESS_EXPIRATION_ERROR);
        }

        //jwt에서 username, role 추출
        String username = jwtService.getUsername(accessToken);
        String role = jwtService.getRole(accessToken);
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        String password = member.getPassword();
        System.out.println(member.getEmail());
        System.out.println(member.getName());
        System.out.println(member.getPassword());

        MemberDto.createDetails memberDto = new MemberDto.createDetails(username, role, password);
        CustomMemberDetails customMemberDetails = new CustomMemberDetails(memberDto);
        System.out.println(customMemberDetails.getUsername());

        Authentication authToken = new UsernamePasswordAuthenticationToken(customMemberDetails, null, customMemberDetails.getAuthorities());
        //일시적인 세션을 생성
        SecurityContextHolder.getContext().setAuthentication(authToken);


        filterChain.doFilter(request, response);
    }
}