package com.project.rentoday.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.rentoday.domain.notification.entity.Notification;
import com.project.rentoday.global.type.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

public class NotificationDto  {

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateRequest implements Serializable {
        //역직렬화 InvalidClassException 방지
        private static final long serialVersionUID = 1L;
        //메시지
        private String message;
        //수신자
        private String receiver;
        //알림유형
        private NotificationType type;

        public CreateRequest(Notification notification) {
            this.message = notification.getMessage();
            this.receiver = notification.getMember().getEmail();
            this.type = notification.getType();
        }
    }

    @Getter
    @Setter
    public static class ReadResponse {
        private String message;
        @JsonFormat(pattern = "yy.MM.dd HH:mm")
        private LocalDateTime date;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class sendResponse {
        private String message;
        @JsonFormat(pattern = "yy.MM.dd HH:mm")
        private LocalDateTime date;
    }

}