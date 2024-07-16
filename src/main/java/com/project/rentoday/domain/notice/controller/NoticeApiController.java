package com.project.rentoday.domain.notice.controller;

import com.project.rentoday.domain.notice.dto.NoticeDto;
import com.project.rentoday.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//공지사항 컨트롤러
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/notice")
public class NoticeApiController {

    private final NoticeService noticeService;

    @PostMapping(value = "/create")
    public ResponseEntity<String> readAll(@AuthenticationPrincipal UserDetails principal,
                                          @RequestBody NoticeDto.CreateRequest createRequest) {
        String email = principal.getUsername();
        noticeService.createNotice(email, createRequest);
        List<NoticeDto.ReadResponse> notices = noticeService.readAllNotice();

        return ResponseEntity.ok().body("공지사항이 작성되었습니다.");
    }

    
    //공지사항 전체조회
    @GetMapping(value = "/readAll")
    public ResponseEntity<List<NoticeDto.ReadResponse>> readAll() {
        List<NoticeDto.ReadResponse> notices = noticeService.readAllNotice();

        return ResponseEntity.ok().body(notices);
    }

    //공지사항 단일조회
    @GetMapping(value = "/{noticeId}")
    public ResponseEntity<NoticeDto.ReadResponse> readOne(@PathVariable Long noticeId) {
        NoticeDto.ReadResponse notice = noticeService.readOneNotice(noticeId);

        return ResponseEntity.ok().body(notice);
    }

    //수정
    @PatchMapping(value = "/{noticeId}")
    public ResponseEntity<String> update(@PathVariable Long noticeId, @RequestBody NoticeDto.UpdateRequest updateRequest) {
        noticeService.updateNotice(noticeId, updateRequest);

        return ResponseEntity.ok().body("공지사항이 수정되었습니다.");
    }

    //삭제
    @DeleteMapping(value = "/{noticeId}")
    public ResponseEntity<String> delete(@PathVariable Long noticeId) {
        noticeService.deleteNotice(noticeId);

        return ResponseEntity.ok().body("공지사항이 삭제되었습니다.");
    }

    //선택 삭제
    @DeleteMapping(value = "/notices")
    public ResponseEntity<String> deleteAll(@PathVariable List<Long> noticeIds) {
        noticeService.deleteAllNotice(noticeIds);

        return ResponseEntity.ok().body("선택한 공지사항이 삭제되었습니다.");
    }
}
