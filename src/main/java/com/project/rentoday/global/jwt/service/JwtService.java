package com.project.rentoday.global.jwt.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    private SecretKey secretKey;

    //객체 secretKey 생성
    public JwtService(@Value("${spring.jwt.secret}")String secret) {

        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8)
                , Jwts
                .SIG
                .HS256
                .key()
                .build()
                .getAlgorithm());
    }

    //jwt에서 사용자 id추출하기
    public String getUsername(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("username", String.class);
    }

    //jwt에서 사용자 권한 추출하기
    public String getRole(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    //jwt 만료시간 체크
    public Boolean isExpired(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 토큰이 이미 만료되어 예외가 발생한 경우
        }
    }

    //jwt 만료시간 체크
    public Date expiredDate(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();
    }

    //jwt access token생성
    public String createAccessJwt(String username, String role) {
        return Jwts
                .builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 10000L))
                .signWith(secretKey)
                .compact();
    }

    //jwt refresh token생성
    public String createRefreshJwt() {
        return Jwts
                .builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 86400000L))
                .signWith(secretKey)
                .compact();
    }
}
