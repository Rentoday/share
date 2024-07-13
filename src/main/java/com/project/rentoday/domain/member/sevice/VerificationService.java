package com.project.rentoday.domain.member.sevice;

import com.project.rentoday.domain.member.dto.EmailDto;
import com.project.rentoday.domain.member.exception.VerificationErrorCode;
import com.project.rentoday.domain.member.exception.VerificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final CertifiedService certifiedService;

    //코드 만료시간 3분
    private final static long ACCESS_CODE_VALIDITY = 180;

    //reids에 코드 저장
    public String redisSave(String email) {
        String accessCode = certifiedService.createAccessCode();
        redisTemplate.opsForValue().set(email, accessCode, ACCESS_CODE_VALIDITY, TimeUnit.SECONDS);
        return accessCode;
    }

    //redis의 인증키 검증
    public void  verification(EmailDto.codeRequest codeRequest) {
        String email = codeRequest.getEmail();
        String accessCode = codeRequest.getAccessCode();
        String redisValue = redisTemplate.opsForValue().get(email);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(email))) {
            System.out.println("키값 존재 유");
            if (accessCode.equals(redisValue)) {
                System.out.println("키값 체크 확인");
                redisTemplate.delete(email);
                redisTemplate.opsForValue().set(email, "TRUE");
                return;
            }
            throw new VerificationException(VerificationErrorCode.VERIFICATION_CODE_NOT_FOUND_ERROR);
        }
        throw new VerificationException(VerificationErrorCode.VERIFICATION_EXPIRATION_CODE_ERROR);
    }

    //이메일 인증 여부 체크
    public boolean verficationCheck(String email) {
        String redisValue = redisTemplate.opsForValue().get(email);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(email))) {
            if (redisValue.equals("TRUE")) {
                redisTemplate.delete(email);
                return true;
            }
            throw new VerificationException(VerificationErrorCode.VERIFICATION_ERROR);
        }
        return false;
    }
}
