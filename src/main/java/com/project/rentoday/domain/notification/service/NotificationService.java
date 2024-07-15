package com.project.rentoday.domain.notification.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.MessageDto;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.notification.repository.NotificationRepository;
import com.project.rentoday.global.type.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    //동시성 이슈 발생 방지를 위해ConcurrentHashMap
    public static final Map<String, SseEmitter> userEmitters = new ConcurrentHashMap<>();

    private final MemberRepository memberRepository;
    private final RedisMessagePublisher redisMessagePublisher;
    private final NotificationRepository notificationRepository;

    //구독 유지 시간 지정
    private final static Long DEFAULT_TIMEOUT = 3600000L;

    //구독
    public SseEmitter subscribe(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        //기존 연결이 있다면 제거
        SseEmitter oldEmitter = userEmitters.remove(email);
        if(oldEmitter != null) {
            oldEmitter.complete();
        }

        //Sse 객체 생성
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        //Sse를 email을 키로 구독 시작
        userEmitters.put(email, sseEmitter);
        //자신의 이메일로 redis topic 구독 시작
        redisMessagePublisher.subcribeTopic(email);

        //연결이 해제될 경우 사용자 id를 삭제
        sseEmitter.onCompletion(() -> {
            userEmitters.remove(email);
            redisMessagePublisher.unSubcribeTopic(email);
        });
        //연결 시간이 만료될 경우 사용자 id를 삭제
        sseEmitter.onTimeout(() -> {
            userEmitters.remove(email);
            redisMessagePublisher.unSubcribeTopic(email);
        });
        //연결 에러가 발생할 경우 사용자 id를 삭제
        sseEmitter.onError((e) -> {
            userEmitters.remove(email);
            redisMessagePublisher.unSubcribeTopic(email);
        });

        // 연결 직후, 데이터 전송이 없을 시 503 에러 발생. 에러 방지 위한 더미데이터 전송
        MessageDto messageDto = new MessageDto("메시지 입니다", email, LocalDateTime.now().toString());
        MessageDto messageDto1 = new MessageDto("메시지 입니다1", email, LocalDateTime.now().toString());
        MessageDto messageDto2 = new MessageDto("메시지 입니다2", email, LocalDateTime.now().toString());
        MessageDto messageDto3 = new MessageDto("메시지 입니다3", email, LocalDateTime.now().toString());
        MessageDto messageDto4 = new MessageDto("메시지 입니다4", email, LocalDateTime.now().toString());
        redisMessagePublisher.publishTopic(email, messageDto);
        redisMessagePublisher.publishTopic(email, messageDto1);
        redisMessagePublisher.publishTopic(email, messageDto2);
        redisMessagePublisher.publishTopic(email, messageDto3);
        redisMessagePublisher.publishTopic(email, messageDto4);

        //클라이언트가 미수신한 메시지 발생시 메시지 전송
        sendUnreadNotifications(member, sseEmitter);
        System.out.println("메시지 발송");


        return sseEmitter;
    }

    //미수신 메시지 전송
    private void sendUnreadNotifications(Member member, SseEmitter sseEmitter) {
        List<Notification> unreadNotifications = notificationRepository.findByMemberAndReadFalseOrderByCreatedAtAsc(member);
        if (!unreadNotifications.isEmpty()) {
            unreadNotifications.forEach(notification -> {
                try {
                    sseEmitter.send(SseEmitter.event().name("notification").data(notification.getMessage()));
                    notification.updateRead();
                } catch (IOException e) {
                    log.error("Error sending notification: ", e);
                }
            });
            notificationRepository.saveAll(unreadNotifications);
        }
    }

    //미수신 메시지 전송
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException e) {
            log.error("SSE 연결 오류 발생", e);
        }
    }

    //로그아웃 시 구독해제
    public void unSubscribe(String email) {
        redisMessagePublisher.unSubcribeTopic(email);
        userEmitters.remove(email);
    }
}