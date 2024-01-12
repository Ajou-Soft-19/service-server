package com.ajousw.spring.domain.member;

import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    public final MemberJpaRepository memberJpaRepository;

    public Member findByEmail(String email) {
        return memberJpaRepository.findByEmail(email).orElseThrow(() -> {
            log.info("해당 이메일로 가입된 계정이 존재하지 않음.");
            return new IllegalArgumentException("해당 이메일로 가입된 계정이 존재하지 않습니다.");
        });
    }

    public Member findByMemberId(Long memberId) {
        return memberJpaRepository.findById(memberId).orElseThrow(() -> {
            log.info("해당 아이디의 멤버가 존재하지 않음");
            return new IllegalArgumentException("해당 아이디의 멤버가 존재하지 않습니다.");
        });
    }
}
