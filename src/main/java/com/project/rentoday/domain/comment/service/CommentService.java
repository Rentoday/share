package com.project.rentoday.domain.comment.service;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.comment.entity.Comment;
import com.project.rentoday.domain.comment.exception.CommentErrorCode;
import com.project.rentoday.domain.comment.exception.CommentException;
import com.project.rentoday.domain.comment.repository.CommentRepository;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notification.dto.NotificationDto;
import com.project.rentoday.domain.notification.service.MessageService;
import com.project.rentoday.domain.notification.service.RedisMessagePublisher;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.exception.ParkIdNotFoundException;
import com.project.rentoday.domain.park.repository.ParkRepository;
import com.project.rentoday.global.type.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
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
        //댓글 작성자
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        //댓글의 상품
        Park park = parkRepository.findById(id)
                .orElseThrow(() -> new ParkIdNotFoundException("해당 주차 공간을 찾을 수 없습니다."));

        List<Comment> comments = commentRepository.findByPark(park);
        List<CommentDto.ReadResponse> readList = new ArrayList<>();

        for (Comment comment : comments) {
            CommentDto.ReadResponse response = new CommentDto.ReadResponse(comment, member);
            readList.add(response);
        }

        return readList;
    }

    //댓글 작성
    @Transactional
    public void createComment(CommentDto.CreateRequest createRequest) {

        //댓글 작성자
        Member member = memberRepository.findByEmail(createRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        //댓글의 상품
        Park park = parkRepository.findById(createRequest.getParkId())
                .orElseThrow(() -> new ParkIdNotFoundException("해당 주차 공간을 찾을 수 없습니다."));
        //댓글 생성
        Comment comment = new Comment(createRequest.getContent(), member, park);
        commentRepository.save(comment);

        //메시지 생성
        NotificationDto.CreateRequest message
                = new NotificationDto.CreateRequest(
                        messageService.commentMessage(member.getName()), park.getMember().getEmail(), NotificationType.COMMENT);
        //메시지 발송
        publisher.publishTopic(park.getMember().getEmail(), message);
    }

    //대댓글 작성
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void createReply(CommentDto.CreateReplyRequest createReplyRequest) {
        
        //대댓글 작성자
        Member member = memberRepository.findByEmail(createReplyRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        //원댓글
        Comment comment = commentRepository.findById(createReplyRequest.getParentId())
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND_ERROR));
        //댓글의 상품
        Park park = parkRepository.findById(comment.getPark().getId())
                .orElseThrow(() -> new ParkIdNotFoundException("해당 주차 공간을 찾을 수 없습니다."));

        Comment reply = new Comment(createReplyRequest.getContent(), member, park, comment);
        commentRepository.save(reply);

        //메시지 생성
        NotificationDto.CreateRequest message
                = new NotificationDto.CreateRequest(
                        messageService.replyMessage(member.getName()), comment.getMember().getEmail(), NotificationType.REPLY);
        //메시지 발송
        publisher.publishTopic(comment.getMember().getEmail(), message);

    }

    //댓글 수정
    @Transactional
    public void updateComment(CommentDto.UpdateRequest updateRequest) {

        Member member = memberRepository.findByEmail(updateRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment comment = commentRepository.findByIdAndMember(updateRequest.getParentId(), member)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        comment.updateComment(updateRequest.getContent());

        commentRepository.save(comment);
    }

    //댓글 삭제
    //depth 구분으로 삭제할것
    @Transactional
    public void deleteComment(CommentDto.DeleteRequest deleteRequest) {

        Member member = memberRepository.findByEmail(deleteRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment comment = commentRepository.findByIdAndMember(deleteRequest.getCommentId(), member)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND_ERROR));

        commentRepository.delete(comment);
    }
}
