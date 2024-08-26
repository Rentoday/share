package com.project.rentoday.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.rentoday.domain.comment.entity.Comment;
import com.project.rentoday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

public class CommentDto {

    //댓글 작성
    @Setter
    @Getter
    @Schema(description = "댓글 작성 요청 DTO")
    public static class CreateRequest {
        //작성자
        private String email;
        //댓글의 판매글
        private Long parkId;
        //댓글 내용
        @NotBlank(message = "1글자 이상의 댓글을 입력해주세요.")
        @Schema(description = "댓글 내용")
        private String content;
    }

    //대댓글 작성
    @Setter
    @Getter
    @Schema(description = "답글 작성 요청 DTO")
    public static class CreateReplyRequest {
        private String email;
        private Long parentId;
        @NotBlank(message = "1글자 이상의 답글을 입력해주세요.")
        @Schema(description = "답글 내용")
        private String content;
    }

    //댓글 조회
    @Setter
    @Getter
    @NoArgsConstructor
    @Schema(description = "댓글 조회 응답 DTO")
    public static class ReadResponse {
        @Schema(description = "댓글 아이디")
        private Long id;
        @Schema(description = "댓글 내용")
        private String content;
        @Schema(description = "댓글 작성자")
        private String name;
        @Schema(description = "대댓글 구분")
        private int depth;
        @Schema(description = "작성된 댓글의 상품아이디")
        private Long parentId;
        @Schema(description = "댓글 작성일")
        @JsonFormat(pattern = "yy.MM.dd")
        private LocalDateTime createdDate;
        @Schema(description = "댓글 작성자인지 체크")
        private Boolean isAuth = false;

        public ReadResponse(Comment comment, Member member) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.name = comment.getMember().getName();
            this.depth = comment.getDepth();
            this.createdDate = comment.getCreatedDate();
            this.parentId = comment.getParent()!=null ? comment.getParent().getId() : null;
            this.isAuth = comment.getMember().equals(member);
        }
    }

    //댓글 수정
    @Setter
    @Getter
    @Schema(description = "댓글 수정 요청 DTO")
    public static class UpdateRequest {
        //작성자
        private String email;
        private Long parentId;
        @NotBlank(message = "1글자 이상의 댓글을 입력해주세요.")
        @Schema(description = "댓글 내용")
        private String content;
    }

    //댓글 삭제
    @Schema(description = "댓글 삭제 요청 DTO")
    @Setter
    @Getter
    @AllArgsConstructor
    public static class DeleteRequest {
        private String email;
        private Long commentId;
    }
}
