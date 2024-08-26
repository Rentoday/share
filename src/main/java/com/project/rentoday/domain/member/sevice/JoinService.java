package com.project.rentoday.domain.member.sevice;

import com.project.rentoday.domain.member.dto.EmailDto;
import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.global.file.service.FileUploadService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

//회원가입 처리를 위한 서비스
@Service
@RequiredArgsConstructor
public class JoinService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final VerificationService verificationService;
    private final FileUploadService fileUploadService;

    //이메일 중복검사 / 이메일 전송
    @Transactional
    public void emailCheck(EmailDto.emailRequest emailRequest) {
        String email = emailRequest.getEmail();
        Boolean isExist = memberRepository.existsByEmail(email);

        if (isExist) {
            throw new MemberException(MemberErrorCode.MEMBER_DUPLICATE_EMAIL_ERROR);
        }
        //인증키 생성
        String accessCode = verificationService.redisSave(email);
        emailService.sendEmail(email, accessCode);
    }

    //회원가입 처리를 위한 메서드
    @Transactional
    public void joinProcess(MemberDto.CreateRequest createRequest) {
        String email = createRequest.getEmail();

        //이메일 중복검사
        if (memberRepository.existsByEmail(email)) {
            throw new MemberException(MemberErrorCode.MEMBER_DUPLICATE_EMAIL_ERROR);
        }

        //이메일 인증코드 체크 여부
        if (verificationService.verficationCheck(email)) {
            Member member = Member.createMember()
                    .email(createRequest.getEmail())
                    //bCrypt로 패스워드 암호화
                    .password(bCryptPasswordEncoder.encode(createRequest.getPassword()))
                    .phone(createRequest.getPhone())
                    .name(createRequest.getName())
                    .profileImage(fileUploadService.uploadProfildImage(createRequest.getProfileImage()))
                    .build();
            //DB에 저장(회원가입 완료)
            memberRepository.save(member);
        }
        System.out.println(createRequest.getEmail() + createRequest.getName());
    }
}
