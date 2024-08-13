package com.project.rentoday.global.oauth.service;

import com.project.rentoday.domain.member.dto.MemberDto;
import com.project.rentoday.domain.member.entity.Member;
import com.project.rentoday.domain.member.exception.MemberNotFoundException;
import com.project.rentoday.domain.member.repository.MemberRepository;
import com.project.rentoday.domain.member.sevice.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2UserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 멤버입니다."));
        MemberDto.CreateDetails memberDto = new MemberDto.CreateDetails(member.getEmail(), member.getRoleType().getName(), member.getPassword());

        if (member != null) {

            return new CustomMemberDetails(memberDto);
        }

        return null;
    }
}

