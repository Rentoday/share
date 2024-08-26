package com.project.rentoday.domain.member.controller;

import com.project.rentoday.domain.member.dto.EmailDto;
import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.sevice.JoinService;
import com.project.rentoday.domain.member.sevice.VerificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//회원가입 컨트롤러
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
@Tag(name = "join", description = "member join API")
public class JoinApiController {

    private final JoinService joinService;
    private final VerificationService verificationService;

    //이메일 중복체크 및 인증메일 전송
    @Operation(summary = "이메일 검사/인증메일", description = "이메일 중복검사와 검사 후 해당 이메일로 인증코드를 전송합니다.")
    @PostMapping(value = "/emailcheck",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> emailCheck(@Valid @RequestBody EmailDto.emailRequest emailRequest) {
        joinService.emailCheck(emailRequest);
        return ResponseEntity.status(HttpStatus.OK).body("이메일이 전송되었습니다.");
    }

    //이메일 인증번호 검증
    @Operation(summary = "이메일 인증코드 체크", description = "입력한 이메일과 인증코드의 일치여부를 검사합니다.")
    @PostMapping(value = "/verification",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> verification(@Valid @RequestBody EmailDto.codeRequest codeRequest) {
        verificationService.verification(codeRequest);
        return ResponseEntity.status(HttpStatus.OK).body("이메일 인증이 완료되었습니다.");
    }

    //회원가입
    @Operation(summary = "회원가입 진행", description = "인증코드 체크여부를 확인하고 입력한 정보를 토대로 회원가입을 진행합니다.")
    @PostMapping(value = "/join",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> join(@Valid @RequestPart(value = "key") MemberDto.CreateRequest createRequest,
                                       @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        createRequest.setProfileImage(profileImage);
        joinService.joinProcess(createRequest);
        return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다.");
    }

}
