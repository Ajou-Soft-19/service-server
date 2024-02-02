package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.repository.WarnRecordRepository;

import com.ajousw.spring.web.controller.dto.warm.WarnInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarnRecordService {
    private final WarnRecordRepository warnRecordRepository;
    private final MemberJpaRepository memberJpaRepository;

    // 시작 시간을 기준으로 어떤 차량이 경고를 받았는지 조회할 때
    public List<WarnInfo> getWarmListWithTimeAfter(String email, Long timeAfter) {
        Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> {
                   log.info("해당 email 의 유저가 존재하지 않음.");
                   return new IllegalArgumentException("해당 email 의 유저가 존재하지 않습니다.");
                });

        validateAdminRole(member);
        Map<Long, WarnInfo> result = new HashMap<Long, WarnInfo>();
        List<WarnRecord> warnRecords = warnRecordRepository.findAllAfterTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeAfter), ZoneId.systemDefault()));
        for (WarnRecord v : warnRecords) {
            if (result.containsKey(v.getWarnRecordId().getCheckPointIndex())) {
                WarnInfo warnInfo = result.get(v.getWarnRecordId().getCheckPointIndex());
                warnInfo.getSessionIds().add(v.getWarnRecordId().getSessionId());
                result.put(v.getWarnRecordId().getCheckPointIndex(), warnInfo);
            } else {
                result.put(v.getWarnRecordId().getCheckPointIndex(), new WarnInfo(v.getWarnRecordId().getCheckPointIndex(), v.getCreatedDate(), new ArrayList<String>(Collections.singleton(v.getWarnRecordId().getSessionId()))));
            }
        }

        return result.values().stream().toList();
    }

    private void validateAdminRole(Member member) {
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한이 없습니다.");
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
    }
}
