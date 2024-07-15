package com.project.rentoday.domain.member.controller;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.sevice.MemberService;
import com.project.rentoday.domain.park.dto.ParkResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        MemberDto.ReadResponse response = memberService.info(email);
        return ResponseEntity.ok().body(response);
    }

    //회원수정
    @PutMapping("/update")
    public ResponseEntity<MemberDto.ReadResponse> getParkByMember(@AuthenticationPrincipal UserDetails principal,
                                      @Valid @RequestPart(value = "key") MemberDto.UpdateRequest updateRequest,
                                      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        String email = principal.getUsername();
        updateRequest.setProfileImage(profileImage);
        MemberDto.ReadResponse response = memberService.update(updateRequest, email);
        return ResponseEntity.ok().body(response);
    }

    //회원수정
    @DeleteMapping("/delete")
    public void getParkByMember(@AuthenticationPrincipal UserDetails principal,
                                  @RequestBody MemberDto.DeleteRequest deleteRequest) throws IOException {
        String email = principal.getUsername();
        memberService.delete(deleteRequest, email);
    }
}
