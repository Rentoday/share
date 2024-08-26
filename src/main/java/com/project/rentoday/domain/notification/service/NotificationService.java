package com.project.rentoday.domain.notification.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.notification.exception.NotificationErrorCode;
import com.project.rentoday.domain.notification.exception.NotificationException;
import com.project.rentoday.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
        userEmitters.remove(email);

        //Sse 객체 생성
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        //Sse를 email을 키로 구독 시작
        userEmitters.put(email, sseEmitter);
        //자신의 이메일로 redis topic 구독 시작
        redisMessagePublisher.subscribeTopic(email);

        //연결이 해제될 경우 사용자 id를 삭제
        sseEmitter.onCompletion(() -> {
            userEmitters.remove(email);
            redisMessagePublisher.unSubscribeTopic(email);
        });
        //연결 시간이 만료될 경우 사용자 id를 삭제
        sseEmitter.onTimeout(() -> {
            userEmitters.remove(email);
            redisMessagePublisher.unSubscribeTopic(email);
        });
        //연결 에러가 발생할 경우 사용자 id를 삭제
        sseEmitter.onError((e) -> {
            userEmitters.remove(email);
            redisMessagePublisher.unSubscribeTopic(email);
        });

        //클라이언트가 미수신한 메시지 발생시 메시지 전송
        sendUnreadNotifications(member);

        return sseEmitter;
    }

    //미수신 메시지 전송
    @Transactional
    private void sendUnreadNotifications(Member member) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadMessages(member.getId());
        if (!unreadNotifications.isEmpty()) {
            unreadNotifications.forEach(notification -> {
                NotificationDto.CreateRequest request = new NotificationDto.CreateRequest(notification);
                redisMessagePublisher.publishTopic(member.getEmail(), request);
                notification.updateRead();
            });
            notificationRepository.saveAll(unreadNotifications);
        }
    }

    //로그아웃 시 구독해제
    public void unSubscribe(String email) {
        redisMessagePublisher.unSubscribeTopic(email);
        userEmitters.remove(email);
    }
    
    //모든 알림 조회
    @Transactional
    public List<NotificationDto.ReadResponse> readAll(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        List<Notification> notificationList = notificationRepository.findByMember(member);
        NotificationDto.ReadResponse readDto = new NotificationDto.ReadResponse();
        List<NotificationDto.ReadResponse> readResponse = null;

        if (notificationList != null) {
            for (Notification notification : notificationList) {
                readDto.setMessage(notification.getMessage());
                readDto.setDate(notification.getCreatedDate());
                readResponse.add(readDto);
            }

            return readResponse;

        } throw new NotificationException(NotificationErrorCode.NOTIFICATION_NOT_FOUND_ERROR);
    }
    
    //알림 삭제
    @Transactional
    public void deleteNotification(List<Long> notificationIds) {

        for (Long notificationId : notificationIds) {
            notificationRepository.deleteById(notificationId);
        }
    }
}