package com.ajousw.spring.domain.auth_request;

import com.ajousw.spring.domain.auth_request.repository.AuthRequestRepository;
import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.web.controller.dto.auth.AuthRequestDto;
import com.ajousw.spring.web.controller.dto.auth.AuthResultDto;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    // TODO: 페이징 처리하기
    public List<AuthRequestDto> getRequestEmergencyRoleList(Member member) {
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("ADMIN권한이 없는 사용자가 ADMIN API 요청함");
            throw new IllegalArgumentException("ADMIN 권한이 없습니다.");
        }

        return authRequestRepository.findAllAndOrderById()
                .stream().map(AuthRequestDto::new)
                .toList();
    }

    public Long requestEmergencyRole(Member member) {
        // case1. 권한이 이미 있음.
        if (member.hasRole(Role.ROLE_EMERGENCY_VEHICLE)) {
            log.info("이미 emergency 권한이 있음.");
            throw new IllegalArgumentException("이미 권한이 있습니다.");
        }
        // case2. 권한이 없음
        // auth_request table에 추가해줌.
        // 이미 요청한 적 있는지 확인
        Optional<Long> optionalAuthRequest = authRequestRepository.findByMemberAndRoleTypeAndIsPending(
                member, Role.ROLE_EMERGENCY_VEHICLE);

        if (optionalAuthRequest.isPresent()) {
            log.info("이미 emergency 권한 요청함");
            return optionalAuthRequest.get();
        }
        AuthRequest authRequest = new AuthRequest(member, Role.ROLE_EMERGENCY_VEHICLE);
        authRequestRepository.save(authRequest);
        return authRequest.getId();
    }

    public AuthResultDto checkRoleApproved(Member member, Long requestId) {
        AuthRequest authRequest = findAuthRequestById(requestId);

        if (!Objects.equals(authRequest.getMember().getId(), member.getId())) {
            throw new IllegalArgumentException("Wrong RequestId");
        }

        if (authRequest.isPending()) {
            return new AuthResultDto(false, "Request is Not Approved");
        }

        if (!authRequest.isApproved()) {
            return new AuthResultDto(false, "Request is Rejected");
        }

        return new AuthResultDto(true, "Request is Approved");
    }

    public void rejectRole(Member member, Long targetId) {
        this.checkValidation(member, targetId);
        AuthRequest authRequest = getAuthRequestByIdAndValidate(targetId);
        authRequest.rejectRole();
    }


    public void approveRole(Member member, Long targetId) {
        this.checkValidation(member, targetId);
        AuthRequest authRequest = getAuthRequestByIdAndValidate(targetId);
        authRequest.approveRole();
        Member targetMember = authRequest.getMember();
        targetMember.addRole(Role.ROLE_EMERGENCY_VEHICLE);
    }

    private AuthRequest getAuthRequestByIdAndValidate(Long targetId) {
        AuthRequest authRequest = findAuthRequestById(targetId);

        if (!authRequest.isPending()) {
            throw new IllegalArgumentException("이미 처리된 권한 상승 요청입니다.");
        }

        return authRequest;
    }

    private AuthRequest findAuthRequestById(Long targetId) {
        return authRequestRepository.findById(targetId).orElseThrow(() -> {
            log.info("존재하지 않는 권한 상승 요청 ID");
            throw new IllegalArgumentException("존재하지 않는 권한 상승 요청 ID 입니다.");
        });
    }

    private void checkValidation(Member member, Long targetId) {
        // 1. 현재 사용자 관리자 권한이 있는지 확인
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한이 없음");
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }

//        // 2. targetId가 테이블에 존재하는지 확인
//        if (!authRequestRepository.existsByMemberAndIsPending(member, true)) {
//            log.info("해당 사용자가 권한 요청을 한 기록이 없음");
//            throw new IllegalArgumentException("해당 사용자가 권한 요청을 한 기록이 없습니다.");
//        }
    }
}
