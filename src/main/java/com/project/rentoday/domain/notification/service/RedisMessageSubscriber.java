package com.project.rentoday.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rentoday.domain.notification.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

//구독자 역할
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private final RedisTemplate<String, Object> template;
    private final ObjectMapper objectMapper;

    public void onMessage(Message message, final byte[] pattern) {
        try {
            //메시지 문자열로 역직렬화
            String publishMessage = template.getStringSerializer().deserialize(message.getBody());
            //메시지를 DTO로 역직렬화
            MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);
            SseEmitter sseEmitter = NotificationService.userEmitters.get(messageDto.getReceiver());
            sseEmitter.send(SseEmitter.event().name("notification").data(messageDto));

        } catch (IOException e) {
            e.getStackTrace();
        }

    }

}
