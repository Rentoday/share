package com.project.rentoday.domain.notice.dto;

import com.project.rentoday.domain.notice.entity.Notice;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class NoticeDto {

    @Getter
    @Setter
    public static class CreateRequest {
        private String title;
        private String content;
    }

    @Getter
    @Setter
    public static class ReadResponse {
        private String title;
        private String content;
        private String createdAt;
    }

    @Getter
    @Setter
    public static class UpdateRequest {
        private String title;
        private String content;
    }

}
