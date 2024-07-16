package com.project.rentoday.domain.notification.controller;

import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationApiController {

    private final NotificationService notificationService;

    //구독 요청
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        log.info("{}의 subscribe 요청", email);
        SseEmitter sseEmitter = notificationService.subscribe(email);

        return ResponseEntity.ok().body(sseEmitter);
    }

    //알림 전체 조회
    @GetMapping("/read")
    public ResponseEntity<List<NotificationDto.ReadResponse>> readAllNotification(@AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        log.info("{}의 알림목록 전체조회 요청", email);
        List<NotificationDto.ReadResponse> readResponse = notificationService.readAll(email);

        return ResponseEntity.ok().body(readResponse);
    }
    
    //알림 삭제
    @DeleteMapping("/notification")
    public ResponseEntity<String> deleteNotification(@PathVariable List<Long> notificationIds) {

        log.info("선택 알림 삭제 요청");
        notificationService.deleteNotification(notificationIds);

        return ResponseEntity.ok().body("알림이 삭제되었습니다.");
    }
}
