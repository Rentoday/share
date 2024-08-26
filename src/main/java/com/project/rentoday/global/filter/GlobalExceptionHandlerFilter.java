package com.project.rentoday.global.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.global.jwt.exception.JwtErrorCode;
import com.project.rentoday.global.jwt.exception.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandlerFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            exceptionHandler(response, e);
        }
    }

    private void exceptionHandler(HttpServletResponse response, Exception e) throws IOException{
        ErrorResponse errorResponse = null;
        HttpStatus httpStatus = null;

        if(e instanceof JwtException) {
            JwtErrorCode errorCode = ((JwtException) e).getJwtErrorCode();
            errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
            httpStatus = errorCode.getHttpStatus();
        } else if(e instanceof MemberException) {
            MemberErrorCode errorCode = ((MemberException) e).getMemberErrorCode();
            errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
            httpStatus = errorCode.getHttpStatus();
        }

        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private record ErrorResponse(String errorCode, String message) {
    }

}
