package com.project.rentoday.domain.comment.controller;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "comment", description = "comment API")
public class CommentApiController {

    private final CommentService commentService;

    //댓글 조회
    @Operation(summary = "댓글 조회", description = "상품페이지에 댓글 전체를 조회합니다.")
    @GetMapping(value = "/{parkId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CommentDto.ReadResponse>> readCommment(@PathVariable(name = "parkId") Long parkId,
                                                                      @AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        List<CommentDto.ReadResponse> responses = commentService.readComment(parkId, email);

        return ResponseEntity.ok().body(responses);
    }

    //댓글 작성
    @Operation(summary = "댓글 작성", description = "상품페이지에 댓글을 작성합니다.")
    @PostMapping(value = "/{parkId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createComment(@AuthenticationPrincipal UserDetails principal
            , @PathVariable("parkId") Long parkId
            , @RequestBody CommentDto.CreateRequest createRequest) {
        createRequest.setEmail(principal.getUsername());
        createRequest.setParkId(parkId);
        commentService.createComment(createRequest);
        return ResponseEntity.ok().body("댓글 작성이 완료되었습니다.");
    }

    //대댓글 작성
    @Operation(summary = "답글 작성", description = "상품페이지에 작성된 댓글에 답글을 작성합니다.")
    @PostMapping(value = "/reply/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createReply(@AuthenticationPrincipal UserDetails principal
            ,@PathVariable("id") Long id
            ,@RequestBody CommentDto.CreateReplyRequest createReplyRequest) {
        createReplyRequest.setEmail(principal.getUsername());
        createReplyRequest.setParentId(id);
        commentService.createReply(createReplyRequest);
        return ResponseEntity.ok().body("답글 작성이 완료되었습니다.");
    }

    //댓글 수정
    @Operation(summary = "댓글 수정", description = "상품페이지에 작성된 댓글을 수정합니다.")
    @PutMapping(value = "/{commentId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> updateComment(@AuthenticationPrincipal UserDetails principal
            ,@PathVariable("commentId") Long commentId
            ,@RequestBody CommentDto.UpdateRequest updateRequest) {
        updateRequest.setEmail(principal.getUsername());
        updateRequest.setParentId(commentId);
        commentService.updateComment(updateRequest);
        return ResponseEntity.ok().body("수정되었습니다.");
    }

    //댓글 삭제
    @Operation(summary = "댓글 삭제", description = "상품페이지에 작성된 댓글을 삭제합니다.")
    @DeleteMapping(value = "/{commentId}",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> deleteComment(@AuthenticationPrincipal UserDetails principal
            ,@PathVariable("commentId") Long commentId) {
        CommentDto.DeleteRequest deleteRequest = new CommentDto.DeleteRequest(principal.getUsername(),commentId);
        commentService.deleteComment(deleteRequest);
        return ResponseEntity.ok().body("삭제되었습니다.");
    }
}
