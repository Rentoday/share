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

        return new MemberDto.ReadResponse(member.getEmail(), member.getName(), member.getPhone(), member.getProfileImage());
    }

    //회원 수정
    public MemberDto.ReadResponse update(MemberDto.UpdateRequest updateRequest, String email) throws IOException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));
        member.updateProfile(bCryptPasswordEncoder.encode(updateRequest.getPassword()), fileUploadService.profileImageUpload(updateRequest.getProfileImage()));
        memberRepository.save(member);

        return new MemberDto.ReadResponse(member.getEmail(), member.getName(), member.getPhone(), member.getProfileImage());
    }

    //회원 탈퇴
    public void delete(MemberDto.DeleteRequest deleteRequest, String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND_ERROR));

        Boolean pass = bCryptPasswordEncoder.matches(deleteRequest.getPassword(), member.getPassword());
        if (pass) {
            memberRepository.delete(member);
        }
        throw new MemberException(MemberErrorCode.MEMBER_INVALID_PASSWORD_ERROR);

    }
}
