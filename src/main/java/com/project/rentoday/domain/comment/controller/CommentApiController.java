package com.project.rentoday.domain.comment.controller;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.SimpleTimeZone;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentApiController {

    private final CommentService commentService;

    //댓글 조회
    @GetMapping(value = "/{id}")
    public ResponseEntity<List<CommentDto.ReadResponse>> readCommment(@PathVariable(name = "id") Long id,
                                                                      @AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        List<CommentDto.ReadResponse> responses = commentService.readComment(id, email);

        return ResponseEntity.ok().body(responses);
    }

    //댓글 작성
    @PostMapping(value = "/{id}", produces = "text/plain; charset=UTF-8")
    public ResponseEntity<String> comment(@AuthenticationPrincipal UserDetails principal
            , @PathVariable("id") Long id
            , @RequestBody CommentDto.CreateRequest createRequest) {
        createRequest.setEmail(principal.getUsername());
        createRequest.setParkId(id);
        commentService.createComment(createRequest);
        return ResponseEntity.ok().body("댓글 작성이 완료되었습니다.");
    }

    //대댓글 작성
    @PostMapping(value = "/reply/{id}", produces = "text/plain; charset=UTF-8")
    public ResponseEntity<String> reComment(@AuthenticationPrincipal UserDetails principal
            ,@PathVariable("id") Long id
            ,@RequestBody CommentDto.CreateReplyRequest createReplyRequest) {
        createReplyRequest.setEmail(principal.getUsername());
        createReplyRequest.setParentId(id);
        commentService.createReply(createReplyRequest);
        return ResponseEntity.ok().body("답글 작성이 완료되었습니다.");
    }

    //댓글 수정
    @PutMapping(value = "/{id}", produces = "text/plain; charset=UTF-8")
    public ResponseEntity<String> updateComment(@AuthenticationPrincipal UserDetails principal
            ,@PathVariable("id") Long id
            ,@RequestBody CommentDto.UpdateRequest updateRequest) {
        updateRequest.setEmail(principal.getUsername());
        updateRequest.setParentId(id);
        commentService.updateComment(updateRequest);
        return ResponseEntity.ok().body("수정되었습니다.");
    }

    //댓글 삭제
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<String> updateComment(@AuthenticationPrincipal UserDetails principal
            ,@PathVariable("id") Long id
            ,@RequestBody CommentDto.DeleteRequest deleteRequest) {
        deleteRequest.setEmail(principal.getUsername());
        deleteRequest.setCommentId(id);
        commentService.deleteComment(deleteRequest);
        return ResponseEntity.ok().body("삭제되었습니다.");
    }
}
