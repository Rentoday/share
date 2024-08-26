package com.project.rentoday.domain.member.controller;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.sevice.MemberService;
import com.project.rentoday.domain.park.dto.ParkResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@Tag(name = "member", description = "member API")
public class MemberApiController {

    private final MemberService memberService;
    
    //회원조회
    @Operation(summary = "회원 조회", description = "특정 회원을 조회 합니다.")
    @GetMapping(value = "/info",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDto.ReadResponse> memberOneRead(@AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        MemberDto.ReadResponse response = memberService.info(email);
        return ResponseEntity.ok().body(response);
    }

    //회원전체조회
    @Operation(summary = "회원 전체조회", description = "전체 회원을 조회 합니다.")
    @GetMapping(value = "/infoAll", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDto.ReadResponse>> memberAllRead() {
        List<MemberDto.ReadResponse> response = memberService.allMember();
        return ResponseEntity.ok().body(response);
    }

    //회원수정
    @Operation(summary = "회원 정보수정", description = "회원 정보를 수정합니다.")
    @PutMapping(value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> memberUpdate(@AuthenticationPrincipal UserDetails principal,
                                      @Valid @RequestPart(value = "key") MemberDto.UpdateRequest updateRequest,
                                      @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) throws IOException {
        updateRequest.setEmail(principal.getUsername());
        updateRequest.setProfileImage(profileImage);
        memberService.update(updateRequest);
        return ResponseEntity.ok().body("회원정보가 수정되었습니다.");
    }

    //회원수정
    @Operation(summary = "회원 정보삭제", description = "회원 정보를 삭제합니다.")
    @DeleteMapping(value = "/delete",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> memberDelete(@AuthenticationPrincipal UserDetails principal,
                                  @RequestBody MemberDto.DeleteRequest deleteRequest) throws IOException {
        String email = principal.getUsername();
        memberService.delete(deleteRequest, email);
        return ResponseEntity.ok().body("탈퇴처리 되었습니다.");
    }
}
