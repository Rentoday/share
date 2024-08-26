package com.project.rentoday.domain.notification.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublisher {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final RedisMessageSubscriber redisMessageSubscriber;
    private final NotificationRepository notificationRepository;

    //topic에 메시지 전송
    public void publishTopic(String topic, NotificationDto.CreateRequest createRequest) {
        publish(new ChannelTopic(topic), createRequest);
    }

    //redis topic 구독
    public void subscribeTopic(String topic) {
        redisMessageListenerContainer.addMessageListener(redisMessageSubscriber, new ChannelTopic(topic));
    }

    //redis topic 구독해제
    public void unSubscribeTopic(String topic) {
        redisMessageListenerContainer.removeMessageListener(redisMessageSubscriber);
    }

    public void publish(ChannelTopic topic, NotificationDto.CreateRequest createRequest) {
        if (isOnline(createRequest)) {
            redisTemplate.convertAndSend(topic.getTopic(), createRequest);
        }
    }

    public boolean isOnline(NotificationDto.CreateRequest createRequest) {
        Member member = memberRepository.findByEmail(createRequest.getReceiver())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        SseEmitter sseEmitter = NotificationService.userEmitters.get(createRequest.getReceiver());
        if (sseEmitter != null) {
            return true;
        } else {
            log.info("{}은 로그아웃 상태", member.getEmail());
            Notification notification = Notification.createMessage()
                    .message(createRequest.getMessage())
                    .member(member)
                    .isRead(false)
                    .type(createRequest.getType())
                    .build();
            notificationRepository.save(notification);
            return false;
        }
    }
}