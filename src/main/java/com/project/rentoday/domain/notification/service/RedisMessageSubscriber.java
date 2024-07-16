package com.project.rentoday.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

//구독자 역할
@Service
@RequiredArgsConstructor
public class RedisMessageSubscriber implements MessageListener {

    private static final Logger log = LoggerFactory.getLogger(RedisMessageSubscriber.class);
    private final RedisTemplate<String, Object> template;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    public void onMessage(Message message, final byte[] pattern) {

            log.info("redis 메시지 수신");
            //메시지 문자열로 역직렬화
            NotificationDto.CreateRequest createRequest = deserialize(message);
            //수신자 조회
            Member member = memberRepository.findByEmail(createRequest.getReceiver())
                    .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
            //수신자 체크해서 수신자 SseEmitter 생성
            SseEmitter sseEmitter = NotificationService.userEmitters.get(createRequest.getReceiver());
            //메시지 발송
            sendSse(sseEmitter, member, createRequest);
    }

    //메시지를 DTO로 역직렬화
    private NotificationDto.CreateRequest deserialize(Message message) {
        try {
            String publishMessage = template.getStringSerializer().deserialize(message.getBody());
            NotificationDto.CreateRequest createRequest = objectMapper.readValue(publishMessage, NotificationDto.CreateRequest.class);

            return createRequest;

        }catch (IOException e) {
            log.info("직렬화 예외 발생 : {}", e.getMessage());
        }

        return null;
    }
    
    //SSE 전송
    @Transactional
    private void sendSse(SseEmitter sseEmitter, Member member, NotificationDto.CreateRequest createRequest) {
        try {
            if (sseEmitter != null) {
                log.info("{}은 로그인 상태", member.getEmail());
                Notification notification = Notification.createMessage()
                        .message(createRequest.getMessage())
                        .member(member)
                        .isRead(true)
                        .type(createRequest.getType())
                        .build();
                notificationRepository.save(notification);
                // JSON 문자열로 직렬화
                NotificationDto.sendResponse sendResponse =
                        new NotificationDto.sendResponse(notification.getMessage(), notification.getCreatedDate());
                String messageDtoJson = objectMapper.writeValueAsString(createRequest);
                sseEmitter.send(SseEmitter.event().name("notification").data(messageDtoJson));
                // JSON 문자열을 SSE 이벤트로 전송
            } else {
                log.info("{}은 로그아웃 상태", member.getEmail());
                Notification notification = Notification.createMessage()
                        .message(createRequest.getMessage())
                        .member(member)
                        .isRead(false)
                        .type(createRequest.getType())
                        .build();
                notificationRepository.save(notification);
            }
        }catch (IOException e) {
            log.info("SSE 예외 발생 : {}", e.getMessage());
        }

    }

}
