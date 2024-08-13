package com.project.rentoday.domain.comment.service;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.comment.entity.Comment;
import com.project.rentoday.domain.comment.repository.CommentRepository;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberNotFoundException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.service.MessageService;
import com.project.rentoday.domain.notification.service.RedisMessagePublisher;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.repository.ParkRepository;
import com.project.rentoday.global.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final ParkRepository parkRepository;
    private final CommentRepository commentRepository;
    private final MessageService messageService;
    private final RedisMessagePublisher publisher;

    //댓글 조회
    @Transactional
    public List<CommentDto.ReadResponse> readComment(Long id, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        Park park = parkRepository.findById(id).orElseThrow(() -> new RuntimeException());
        List<Comment> comments = commentRepository.findByPark(park);
        List<CommentDto.ReadResponse> readList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto.ReadResponse response = new CommentDto.ReadResponse();
            response.setId(comment.getId());
            response.setName(comment.getMember().getName());
            response.setContent(comment.getContent());
            response.setCreatedAt(comment.getCreatedDate());
            response.setDepth(comment.getDepth());
            if (comment.getParent() != null) {
                response.setParentId(comment.getParent().getId());
            }
            if (comment.getMember().equals(member)) {
                response.setIsAuth(true);
            }
            readList.add(response);
        }

        return readList;
    }

    //댓글 작성
    @Transactional
    public void createComment(CommentDto.CreateRequest createRequest) {

        Member member = memberRepository.findByEmail(createRequest.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        Park park = parkRepository.findById(createRequest.getParkId())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        Comment comment = Comment.createComment()
                .member(member)
                .park(park)
                .content(createRequest.getContent())
                .build();
        commentRepository.save(comment);

        //메시지 생성
        NotificationDto.CreateRequest message
                = new NotificationDto.CreateRequest(
                        messageService.commentMessage(member.getName()), park.getMember().getEmail(), NotificationType.COMMENT);
        //메시지 발송
        publisher.publishTopic(park.getMember().getEmail(), message);
    }

    //대댓글 작성
    @Transactional
    public void createReply(CommentDto.CreateReplyRequest createReplyRequest) {
        
        //대댓글 작성자
        Member member = memberRepository.findByEmail(createReplyRequest.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        //원댓글
        Comment comment = commentRepository.findById(createReplyRequest.getParentId())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));

        Park park = parkRepository.findById(comment.getPark().getId())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        Comment commentEntity = Comment.createReply()
                .member(member)
                .park(park)
                .content(createReplyRequest.getContent())
                .build();
        commentEntity.setDepth();
        commentEntity.setParent(comment);
        commentRepository.save(commentEntity);

        //메시지 생성
        NotificationDto.CreateRequest message
                = new NotificationDto.CreateRequest(
                        messageService.replyMessage(createReplyRequest.getEmail()), comment.getMember().getEmail(), NotificationType.REPLY);
        //메시지 발송
        publisher.publishTopic(comment.getMember().getEmail(), message);

    }

    //댓글 수정
    @Transactional
    public void updateComment(CommentDto.UpdateRequest updateRequest) {

        Member member = memberRepository.findByEmail(updateRequest.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        Comment comment = commentRepository.findByIdAndMember(updateRequest.getParentId(), member)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        comment.updateComment(updateRequest.getContent());
        commentRepository.save(comment);
    }

    //댓글 삭제
    //depth 구분으로 삭제할것
    @Transactional
    public void deleteComment(CommentDto.DeleteRequest deleteRequest) {

        Member member = memberRepository.findByEmail(deleteRequest.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        Comment comment = commentRepository.findByIdAndMember(deleteRequest.getCommentId(), member)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        commentRepository.delete(comment);
    }
}
