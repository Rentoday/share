package com.project.rentoday.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.MessageDto;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.notification.repository.NotificationRepository;
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
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public void onMessage(Message message, final byte[] pattern) {
        try {
            //메시지 문자열로 역직렬화
            System.out.println("redis 온메시지 진입");
            String publishMessage = template.getStringSerializer().deserialize(message.getBody());
            System.out.println("publishMessage: " + publishMessage);
            System.out.println(message.getBody());
            //메시지를 DTO로 역직렬화
            MessageDto messageDto = objectMapper.readValue(publishMessage, MessageDto.class);
            System.out.println(messageDto.getMessage());
            SseEmitter sseEmitter = NotificationService.userEmitters.get(messageDto.getReceiver());

            Member member = memberRepository.findByEmail(messageDto.getReceiver())
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
            if (sseEmitter != null) {
                System.out.println("sse 구독자");
                String messageDtoJson = objectMapper.writeValueAsString(messageDto);
                // JSON 문자열을 SSE 이벤트로 전송
                sseEmitter.send(SseEmitter.event().name("notification").data(messageDtoJson));
                Notification notification = Notification.createMessage()
                        .message(messageDto.getMessage())
                        .member(member)
                        .createdAt(messageDto.getDate())
                        .read(true)
                        .build();
                notificationRepository.save(notification);
            } else {
                System.out.println("sse 미구독자");
                Notification notification = Notification.createMessage()
                        .message(messageDto.getMessage())
                        .member(member)
                        .createdAt(messageDto.getDate())
                        .read(false)
                        .build();
                notificationRepository.save(notification);
            }
        } catch (IOException e) {
            System.out.println("에러발생: " + e.getMessage());
            e.getStackTrace();
        }

    }

}
