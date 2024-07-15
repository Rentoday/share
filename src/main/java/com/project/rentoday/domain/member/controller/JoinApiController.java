package com.project.rentoday.domain.member.controller;

import com.project.rentoday.domain.member.dto.EmailDto;
import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.sevice.JoinService;
import com.project.rentoday.domain.member.sevice.VerificationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//회원가입 컨트롤러
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class JoinApiController {

    private final JoinService joinService;
    private final VerificationService verificationService;

    //이메일 중복체크 및 인증메일 전송
    @PostMapping(value = "/emailCheck", produces = "text/plain; charset=UTF-8")
    public ResponseEntity<String> emailCheck(@RequestBody EmailDto.Request emailRequest) throws MessagingException {
        joinService.emailCheck(emailRequest);
        return ResponseEntity.status(HttpStatus.OK).body("이메일이 전송되었습니다.");
    }

    //이메일 인증번호 검증
    @PostMapping(value = "/verification", produces = "json/plain; charset=UTF-8")
    public ResponseEntity<String> verification(@RequestBody EmailDto.codeRequest codeRequest) {
        verificationService.verification(codeRequest);
        return ResponseEntity.status(HttpStatus.OK).body("이메일 인증이 완료되었습니다.");
    }

    //회원가입
    @PostMapping(value = "/join")
    public ResponseEntity<String> join(@Valid @RequestPart(value = "key") MemberDto.CreateRequest createRequest,
                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        createRequest.setProfileImage(profileImage);
        joinService.joinProcess(createRequest);
        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다.");
    }

}
