package com.project.rentoday.domain.member.sevice;

import com.project.rentoday.domain.member.exception.EmailErrorCode;
import com.project.rentoday.domain.member.exception.EmailException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    //email 인증번호 발송
    public void sendEmail(String email, String code) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setTo(email);
            helper.setFrom("byn1022@naver.com");
            helper.setSubject("오늘만세워 가입을 완료해주세요.");
            String text = "해당 코드를 입력 확인란에 입력해주세요 : " + code;
            helper.setText(text, true); // true: HTML 포맷 사용 여부
            mailSender.send(mimeMessage);

        }catch (MessagingException e) {
            throw new EmailException(EmailErrorCode.EMAIL_SEND_ERROR);
        }
    }
}
