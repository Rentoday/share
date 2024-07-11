package com.project.rentoday.domain.member.sevice;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class CertifiedService {

    //무작위 난수 생성(6자리)
    public String createAccessCode() {
        List<String> item = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 6; i++) {
            int n = random.nextInt(36);
            if (n < 10) {
                item.add(String.valueOf(n)); // 숫자인 경우
            } else {
                char c = (char) (n - 10 + 'A'); // 영어 대문자인 경우
                item.add(String.valueOf(c));
            }
        }
        return String.join("", item); // 리스트를 문자열로 변환하여 반환
    }
}