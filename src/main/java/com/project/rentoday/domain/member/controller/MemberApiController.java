package com.project.rentoday.domain.member.controller;

import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.sevice.MemberService;
import com.project.rentoday.domain.park.dto.ParkResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/member")
public class MemberApiController {

    private final MemberService memberService;
    
    //회원조회
    @GetMapping("/info")
    public ResponseEntity<MemberDto.ReadResponse> getParkByMember(@AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        MemberDto.ReadResponse response = memberService.memberInfo(email);
        return ResponseEntity.ok().body(response);
    }
}
