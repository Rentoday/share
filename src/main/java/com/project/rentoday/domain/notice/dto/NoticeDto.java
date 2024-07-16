package com.project.rentoday.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.rentoday.domain.notice.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class NoticeDto {

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CreateRequest {
        private String title;
        private String content;
    }

    @Getter
    @Setter
    @JsonFormat(pattern = "yy.MM.dd")
    @NoArgsConstructor
    public static class ReadResponse {
        private String title;
        private String content;
        private LocalDateTime createdAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UpdateRequest {
        private String title;
        private String content;
    }

}
