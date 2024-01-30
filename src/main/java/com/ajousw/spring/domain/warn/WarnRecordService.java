package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.repository.WarnRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarnRecordService {
    private final WarnRecordRepository warnRecordRepository;
    private final MemberJpaRepository memberJpaRepository;

    // 시작 시간을 기준으로 어떤 차량이 경고를 받았는지 조회할 때
    public List<WarnRecord> getWarmListWithTimeAfter(String email, Long timeAfter ) {
        Member member = memberJpaRepository.findByEmail(email).get();
        validateAdminRole(member);
        List<WarnRecord> result = warnRecordRepository.findAllAfterTime(new Timestamp(timeAfter).toLocalDateTime());
        return result;
    }

    private void validateAdminRole(Member member) {
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한이 없습니다.");
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
    }
}
