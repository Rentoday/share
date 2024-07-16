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
import com.project.rentoday.global.type.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
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
        NotificationDto.CreateRequest notificationDto = new NotificationDto.CreateRequest("메시지 입니다", email, NotificationType.COMMENT);
        NotificationDto.CreateRequest notificationDto1 = new NotificationDto.CreateRequest("메시지 입니다1", email, NotificationType.COMMENT);
        NotificationDto.CreateRequest notificationDto2 = new NotificationDto.CreateRequest("메시지 입니다2", email, NotificationType.COMMENT);
        NotificationDto.CreateRequest notificationDto3 = new NotificationDto.CreateRequest("메시지 입니다3", email, NotificationType.COMMENT);
        NotificationDto.CreateRequest notificationDto4 = new NotificationDto.CreateRequest("메시지 입니다4", email, NotificationType.COMMENT);
        redisMessagePublisher.publishTopic(email, notificationDto);
        redisMessagePublisher.publishTopic(email, notificationDto1);
        redisMessagePublisher.publishTopic(email, notificationDto2);
        redisMessagePublisher.publishTopic(email, notificationDto3);
        redisMessagePublisher.publishTopic(email, notificationDto4);

        //클라이언트가 미수신한 메시지 발생시 메시지 전송
//        sendUnreadNotifications(member, sseEmitter);
        System.out.println("메시지 발송");

        return sseEmitter;
    }

    //미수신 메시지 전송
    private void sendUnreadNotifications(Member member, SseEmitter sseEmitter) {
        List<Notification> unreadNotifications = notificationRepository.findUnreadMessages(member);
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

    //로그아웃 시 구독해제
    public void unSubscribe(String email) {
        redisMessagePublisher.unSubcribeTopic(email);
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