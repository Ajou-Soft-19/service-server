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

    public Optional<Member> findOne(String email) {
        return memberJpaRepository.findByEmail(email);
    }
}
