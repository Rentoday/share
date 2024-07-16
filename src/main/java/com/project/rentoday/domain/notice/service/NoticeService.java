package com.project.rentoday.domain.notice.service;

import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.notice.dto.NoticeDto;
import com.project.rentoday.domain.notice.entity.Notice;
import com.project.rentoday.domain.notice.exception.NoticeErrorCode;
import com.project.rentoday.domain.notice.exception.NoticeException;
import com.project.rentoday.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    //공지 전체 조회
    public List<NoticeDto.ReadResponse> readAllNotice() {
        List<Notice> notices = noticeRepository.findAll();
        List<NoticeDto.ReadResponse> noticesDto = new ArrayList<>();
        for (Notice notice : notices) {
            NoticeDto.ReadResponse noticeDto = new NoticeDto.ReadResponse();
            noticeDto.setTitle(notice.getTitle());
            noticeDto.setContent(notice.getContent());
            noticeDto.setCreatedAt(notice.getCreatedDate());
            noticesDto.add(noticeDto);
        }
        System.out.println(noticesDto.get(0));

        return noticesDto;
    }
    
    //공지 조회
    public NoticeDto.ReadResponse readOneNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND_ERROR));
        NoticeDto.ReadResponse response = new NoticeDto.ReadResponse();
        response.setTitle(notice.getTitle());
        response.setContent(notice.getContent());
        response.setCreatedAt(notice.getCreatedDate());

        return response;
    }

    //공지 작성
    public void createNotice(String email, NoticeDto.CreateRequest request) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        Notice notice = Notice.createNotice()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();
        noticeRepository.save(notice);
    }
    
    //공지 수정
    public void updateNotice(Long noticeId, NoticeDto.UpdateRequest request) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NoticeException(NoticeErrorCode.NOTICE_NOT_FOUND_ERROR));
        notice.updateNotice(request.getTitle(), request.getContent());
    }

    //공지 삭제
    public void deleteNotice(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }
    
    //공지 선택 삭제
    public void deleteAllNotice(List<Long> noticeIds) {
        for (Long noticeId : noticeIds) {
            noticeRepository.deleteById(noticeId);
        }
    }

}
