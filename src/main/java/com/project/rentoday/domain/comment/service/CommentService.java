package com.project.rentoday.domain.comment.service;

import com.project.rentoday.domain.comment.dto.CommentDto;
import com.project.rentoday.domain.comment.entity.Comment;
import com.project.rentoday.domain.comment.repository.CommentRepository;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.park.entity.Park;
import com.project.rentoday.domain.park.repository.ParkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final ParkRepository parkRepository;
    private final CommentRepository commentRepository;

    //댓글 작성
    @Transactional
    public void createComment(CommentDto.CreateRequest createRequest) {

        Member member = memberRepository.findByEmail(createRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Park park = parkRepository.findById(createRequest.getParkId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment comment = Comment.createComment()
                .member(member)
                .park(park)
                .content(createRequest.getContent())
                .build();
        commentRepository.save(comment);
    }

    //대댓글 작성
    @Transactional
    public void createReply(CommentDto.CreateReplyRequest createReplyRequest) {

        Member member = memberRepository.findByEmail(createReplyRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment comment = commentRepository.findByIdAndMemberId(createReplyRequest.getParentId(), member.getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Park park = parkRepository.findById(comment.getPark().getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment commentEntity = Comment.createReply()
                .member(member)
                .park(park)
                .content(createReplyRequest.getContent())
                .parent(comment)
                .build();
        commentRepository.save(commentEntity);
    }

    //댓글 수정
    @Transactional
    public void updateComment(CommentDto.UpdateRequest updateRequest) {

        Member member = memberRepository.findByEmail(updateRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment comment = commentRepository.findByIdAndMemberId(updateRequest.getParentId(), member.getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        comment.updateComment(updateRequest.getContent());
        commentRepository.save(comment);
    }

    //댓글 삭제
    //depth 구분으로 삭제할것
    @Transactional
    public void delateComment(CommentDto.DeleteRequest deleteRequest) {

        Member member = memberRepository.findByEmail(deleteRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Comment comment = commentRepository.findByIdAndMemberId(deleteRequest.getParentId(), member.getId())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        commentRepository.delete(comment);
    }
}
