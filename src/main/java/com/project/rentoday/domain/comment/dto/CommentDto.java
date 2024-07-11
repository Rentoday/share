package com.project.rentoday.domain.comment.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentDto {

    //댓글 작성
    @Setter
    @Getter
    public static class CreateRequest {
        private String email;
        private Long parkId;
        private String content;
    }

    //댓글 조회
    @Setter
    @Getter
    public static class ReadResponse {
        private String content;
    }

    //댓글 수정
    @Setter
    @Getter
    public static class UpdateRequest {
        private String email;
        private Long parentId;
        private String content;
    }

    //댓글 삭제
    @Setter
    @Getter
    public static class DeleteRequest {
        private String email;
        private Long parentId;
    }

    //대댓글 작성
    @Setter
    @Getter
    public static class CreateReplyRequest {
        private String email;
        private Long parentId;
        private String content;
    }
}
