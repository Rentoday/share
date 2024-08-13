package com.project.rentoday.global.jwt.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.global.jwt.dto.JwtDto;
import com.project.rentoday.global.jwt.entity.RefreshToken;
import com.project.rentoday.global.jwt.exception.JwtErrorCode;
import com.project.rentoday.global.jwt.exception.JwtException;
import com.project.rentoday.global.jwt.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final JwtService jwtService;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public JwtDto reissue(String refreshToken) {

        //저장한 refresh의 값이 null이라면
        if (refreshToken == null) {

            throw new JwtException(JwtErrorCode.JWT_REFRESH_NOT_FOUND_ERROR);
        }

        //refresh Token의 시간이 만료되는지 체크
        if (jwtService.isExpired(refreshToken)) {

            throw new JwtException(JwtErrorCode.JWT_REFRESH_EXPIRATION_ERROR);
        }

        //DB에 refresh Token이 저장되어있는지 확인
        Boolean isExist = refreshRepository.existsByRefreshToken(refreshToken);
        if (!isExist) {

            throw new JwtException(JwtErrorCode.JWT_REFRESH_NOT_FOUND_ERROR);
        }

        RefreshToken refresh = refreshRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException(JwtErrorCode.JWT_REFRESH_NOT_FOUND_ERROR));
        Member member = refresh.getMember();
        String email = member.getEmail();
        String role = member.getRoleType().getName();

        //Access Token 재발급
        String newAccess = jwtService.createAccessJwt(email, role);

        JwtDto jwtDto = new JwtDto();
        jwtDto.setAccessToken(newAccess);
        return jwtDto;
    }

    @Transactional
    public void deleteRefresh(String refresh) {
        refreshRepository.deleteByRefreshToken(refresh);
    }

//    private void saveRefreshToken(String email, String refreshToken) {
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
//        Date date = new Date(System.currentTimeMillis() + 86400000L);
//        RefreshToken refresh = new RefreshToken(refreshToken, member, date.toString());
//        refreshRepository.save(refresh);
//    }
}
