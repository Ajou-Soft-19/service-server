package com.ajousw.spring.domain.member;

import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.web.controller.dto.member.EmergencyMemberDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    public final MemberJpaRepository memberJpaRepository;
    /* find emergency user */
    public List<EmergencyMemberDto> findEmergencyMember(Member member) {
        // amdin 확인
        checkAdmin(member);
        List<EmergencyMemberDto> result = new ArrayList<EmergencyMemberDto>();

        memberJpaRepository.findAll()
                .forEach(v -> {
                            if (v.hasRole(Role.ROLE_EMERGENCY_VEHICLE)) {
                                result.add(new EmergencyMemberDto(v.getId(), v.getUsername()));
                            }
                });
        return result;
    }


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

    public void checkRoleEmergencyAndWait(Member member) {
        if (member.hasRole(Role.ROLE_EMERGENCY_VEHICLE)) {
            log.info("이미 권한을 가지고 있는 멤버임");
            throw new IllegalArgumentException("이미 권한을 가지고 있습니다.");
        }

        if (member.hasRole(Role.ROLE_WAIT)) {
            log.info("이미 권한 승인을 요청함");
            throw new IllegalArgumentException("이미 권한 승인 요청을 하였습니다.");
        }
    }

    public void addRole(Long userId, Role role) {
        Member member = this.findByMemberId(userId);
        this.checkRoleEmergencyAndWait(member);
        member.addRole(role);
        memberJpaRepository.save(member);
    }

    public String removeRole(Member member, Long targetId, Role role) {
        Member targetMember = this.findByMemberId(targetId);
        this.checkAdmin(member);
        if (!targetMember.hasRole(role)) {
            log.info("사용자가 보유하지 않은 권한을 삭제하려고 함.");
            throw new IllegalArgumentException("없는 권한입니다.");
        } else {
            targetMember.removeRole(role);
            memberJpaRepository.save(targetMember);
            return targetMember.getRoles();
        }
    }

    public String approveRole(String email, Long targetId) {
        Member member = this.findByEmail(email);
        Member targetMember = this.findByMemberId(targetId);
        this.checkAdmin(member);

        targetMember.addRole(Role.ROLE_EMERGENCY_VEHICLE);
        memberJpaRepository.save(targetMember);

        return targetMember.getRoles();
    }

    public void checkAdmin(Member member) {
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("admin 권한이 없음.");
            throw new IllegalArgumentException("admin 권한이 없습니다.");
        }
    }
}
