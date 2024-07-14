package com.project.rentoday.domain.notification.dto;

import com.project.rentoday.global.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class MessageDto implements Serializable {

    //역직렬화 InvalidClassException 방지
    private static final long serialVersionUID = 1L;

    //메시지
    private String message;
    //수신자
    private String receiver;
    //메시지 타입
    private NotificationType type;
    //메시지 날짜
    private LocalDateTime date;


}