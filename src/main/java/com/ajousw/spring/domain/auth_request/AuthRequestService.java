package com.ajousw.spring.domain.auth_request;

import com.ajousw.spring.domain.auth_request.repository.AuthRequestRepository;
import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthRequestService {
    private final AuthRequestRepository authRequestRepository;

    public void addRole(Member member) {
        // case1. 권한이 이미 있음.
        if (member.hasRole(Role.ROLE_EMERGENCY_VEHICLE)) {
            log.info("이미 emergency 권한이 있음.");
            throw new IllegalArgumentException("이미 권한이 있습니다.");
        }
        // case2. 권한이 없음
        // auth_request table에 추가해줌.
        // 이미 요청한 적 있는지 확인
        if (authRequestRepository.existsByUserId(member.getId())) {
            log.info("이미 emergency 권한 요청함");
            throw new IllegalArgumentException("이미 권한을 요청하였습니다.");
        }
        AuthRequest authRequest = new AuthRequest(member.getId());
        authRequestRepository.save(authRequest);
    }

    public void rejectRole(Member member, Long targetId) {
        this.checkValidation(member, targetId);
        authRequestRepository.deleteAuthRequestByUserId(targetId);
    }

    public void approveRole(Member member, Long targetId) {
        this.checkValidation(member, targetId);

        // 3. 테이블에 존재하면 승인
        // 3-1. 테이블에서 삭제하고
        // 3-2. Member 테이블에 ROLE_ADMIN 추가
        authRequestRepository.deleteAuthRequestByUserId(targetId);
    }

    private void checkValidation(Member member, Long targetId) {
        // 1. 현재 사용자 관리자 권한이 있는지 확인
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한이 없음");
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }

        // 2. targetId가 테이블에 존재하는지 확인
        if (!authRequestRepository.existsByUserId(targetId)) {
            log.info("해당 사용자가 권한 요청을 한 기록이 없음");
            throw new IllegalArgumentException("해당 사용자가 권한 요청을 한 기록이 없습니다.");
        }
    }
}
