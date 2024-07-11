package com.project.rentoday.global.jwt.controller;

import com.project.rentoday.global.jwt.dto.JwtDto;
import com.project.rentoday.global.jwt.repository.RefreshRepository;
import com.project.rentoday.global.jwt.service.JwtService;
import com.project.rentoday.global.jwt.service.RefreshService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final RefreshService refreshService;

    @GetMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        //request로 부터 Cookie를 받아온다,
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            //받아온 cookie에서 refresh를 찾아 refresh 변수에 해당 값을 저장한다.
            if (cookie.getName().equals("Refresh")) {
                refreshToken = cookie.getValue();
            }
        }

        JwtDto jwtDto = refreshService.reissue(refreshToken);
        String newAccess = jwtDto.getAccessToken();
        String newRefresh = jwtDto.getRefreshToken();

        //response
        response.addHeader("Authorization", "Bearer " + newAccess);
        response.addCookie(createCookie("Refresh", newRefresh));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
//        cookie.getSecure(true);
//        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;

    }

}
