package com.project.rentoday.domain.notification.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.MessageDto;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
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
        //Sse 객체 생성
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        //Sse를 email을 키로 구독 시작
        userEmitters.put(email, sseEmitter);
        //자신의 이메일로 redis topic 구독 시작
        redisMessagePublisher.subcribeTopic(email);

        //연결이 해제될 경우 사용자 id를 삭제
        sseEmitter.onCompletion(() -> userEmitters.remove(email));
        //연결 시간이 만료될 경우 사용자 id를 삭제
        sseEmitter.onTimeout(() -> userEmitters.remove(email));
        //연결 에러가 발생할 경우 사용자 id를 삭제
        sseEmitter.onError((e) -> userEmitters.remove(email));

        //클라이언트가 미수신한 메시지 발생시 메시지 전송
        Boolean isExist = notificationRepository.existsByMember(member);
        if (isExist) {
            List<Notification> notifications = notificationRepository.findByMember(member);
            for (Notification notification : notifications) {
                try {
                    sseEmitter.send(SseEmitter.event().name("notification").data(notification.getMessage()));
                }catch (IOException e) {
                    e.getStackTrace();
                }
            }
        }

        return sseEmitter;
    }

    //메시지 전송
    public void sendNotification(String receiverId, MessageDto messageDto){
        try {
            SseEmitter sseEmitter = userEmitters.get(receiverId);
            sseEmitter.send(SseEmitter.event().name("notification").data(messageDto));
        }catch (IOException e) {
            e.getStackTrace();
        }
    }

    //로그아웃 시 구독해제
    public void unSubscribe(String email) {
        redisMessagePublisher.unSubcribeTopic(email);
        userEmitters.remove(email);
    }
}