package com.project.rentoday.domain.notification.service;

import com.project.rentoday.domain.notification.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final RedisTemplate<String, Object> redisTemplate;

    private final RedisMessageSubscriber redisMessageSubscriber;

    //topic에 메시지 전송
    public void publishTopic(String topic, NotificationDto.CreateRequest createRequest) {
        publish(new ChannelTopic(topic), createRequest);
    }

    //redis topic 구독
    public void subcribeTopic(String topic) {
        redisMessageListenerContainer.addMessageListener(redisMessageSubscriber, new ChannelTopic(topic));
    }

    //redis topic 구독해제
    public void unSubcribeTopic(String topic) {
        redisMessageListenerContainer.removeMessageListener(redisMessageSubscriber);
    }

    public void publish(ChannelTopic topic, NotificationDto.CreateRequest createRequest) {
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        System.out.println("메시지 전송@@@@");
        redisTemplate.convertAndSend(topic.getTopic(), createRequest);
    }

}