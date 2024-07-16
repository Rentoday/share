package com.project.rentoday.global.filter;

import com.project.rentoday.domain.notification.service.NotificationService;
import com.project.rentoday.global.jwt.exception.JwtErrorCode;
import com.project.rentoday.global.jwt.exception.JwtException;
import com.project.rentoday.global.jwt.repository.RefreshRepository;
import com.project.rentoday.global.jwt.service.JwtService;
import com.project.rentoday.global.jwt.service.RefreshService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class LogoutFilter extends GenericFilterBean {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final NotificationService notificationService;
    private final RefreshService refreshService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        doFilter((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse, filterChain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            System.out.println("로그아웃 진입");
            //요청받은 uri 경로가 /logout인지 검증
            String requestUri = request.getRequestURI();
            if (!requestUri.matches("^\\/logout$")) {

                //아닐 경우 다음 필터로 이동
                filterChain.doFilter(request, response);
                return;
            }

            //요청받은 method가 post인지 검증
            String requestMethod = request.getMethod();
            if (!requestMethod.equals("POST")) {

                //post 요청이 아니면 다음 필터로 이동
                filterChain.doFilter(request, response);
                return;
            }

            String accessToken = request.getHeader("Authorization").split(" ")[1];
            String email = jwtService.getUsername(accessToken);

            //cookie에서 refresh token을 체크
            String refresh = null;

            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("Refresh")) {
                    System.out.println(cookie.getValue());
                    refresh = cookie.getValue();
                }
            }

            System.out.println(refresh);

            //refresh null check
            if (refresh == null) {

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            //expired check
            if(jwtService.isExpired(refresh)) {
                //response status code
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }

            //DB에 저장되어 있는지 확인
            Boolean isExist = refreshRepository.existsByRefreshToken(refresh);
            if (!isExist) {

                //response status code
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            //로그아웃 진행
            //Refresh 토큰 DB에서 제거
            refreshService.deleteRefresh(refresh);
            notificationService.unSubscribe(email);
            System.out.println("로그아웃");

            //Refresh token cookie 값 0
            Cookie cookie = new Cookie("Refresh", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");

            response.addCookie(cookie);
            System.out.println("Authentication successful. Redirecting to: http://localhost:81/main");
            response.sendRedirect("http://localhost:81/main");
        } catch (JwtException e) {
            JwtException(response, e);
        }
    }

    private void JwtException(HttpServletResponse response, JwtException e) throws IOException {
        JwtErrorCode errorCode = e.getJwtErrorCode();
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType("application/json");
        response.getWriter().write("{\"errorCode\": \"" + errorCode.getCode() + "\", \"message\": \"" + errorCode.getMessage() + "\"}");
    }

}
