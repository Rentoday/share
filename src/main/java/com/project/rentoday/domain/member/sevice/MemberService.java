package com.project.rentoday.domain.member.sevice;

import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberErrorCode;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.global.file.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileUploadService fileUploadService;

    //회원 조회
    public MemberDto.ReadResponse info(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        return new MemberDto.ReadResponse(member);
    }

    //회원전체 조회
    public List<MemberDto.ReadResponse> allMember() {

        List<Member> members = memberRepository.findAll();
        List<MemberDto.ReadResponse> memberList = null;

        if (members != null) {
            for(Member member : members) {
                MemberDto.ReadResponse readDto = new MemberDto.ReadResponse(member);
                memberList.add(readDto);
            }
            return memberList;
        }
        throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR);
    }

    //회원 수정
    public void update(MemberDto.UpdateRequest updateRequest) throws IOException {

        Member member = memberRepository.findByEmail(updateRequest.getEmail())
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        member.updateProfile(bCryptPasswordEncoder.encode(updateRequest.getPassword()), fileUploadService.uploadProfildImage(updateRequest.getProfileImage()));
        memberRepository.save(member);
    }

    //회원 탈퇴
    public void delete(MemberDto.DeleteRequest deleteRequest, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        boolean pass = bCryptPasswordEncoder.matches(deleteRequest.getPassword(), member.getPassword());

        if (pass) {
            memberRepository.delete(member);
        }
        throw new MemberException(MemberErrorCode.MEMBER_INVALID_PASSWORD_ERROR);
    }
}
