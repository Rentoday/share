package com.project.rentoday.domain.notice.controller;

import com.project.rentoday.domain.notice.dto.NoticeDto;
import com.project.rentoday.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//공지사항 컨트롤러
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/notice")
public class NoticeApiController {

    private final NoticeService noticeService;

    @GetMapping(value = "/readall")
    public ResponseEntity<List<NoticeDto.ReadResponse>> readAll() {
        List<NoticeDto.ReadResponse> notices = noticeService.readAllNotice();

        return ResponseEntity.ok().body(notices);
    }

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
    @DeleteMapping(value = "/delete")
    public ResponseEntity<String> deleteAll(@PathVariable List<Long> noticeIds) {
        noticeService.deleteAllNotice(noticeIds);

        return ResponseEntity.ok().body("선택한 공지사항이 삭제되었습니다.");
    }
}
