package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.warn.entity.EmergencyEvent;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.repository.EmergencyEventRepository;
import com.ajousw.spring.domain.warn.entity.repository.WarnRecordRepository;

import com.ajousw.spring.web.controller.dto.warm.WarnInfo;
import com.ajousw.spring.web.controller.dto.warm.WarnListEmergencyRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarnRecordService {
    private final WarnRecordRepository warnRecordRepository;
    private final EmergencyEventRepository emergencyEventRepository;
    private final MemberJpaRepository memberJpaRepository;

    public List<WarnInfo> getWarmListWithTimeAfterAndEmergencyEventId(String email, WarnListEmergencyRequestDto warnListEmergencyRequestDto) {
        validateAdminRole(email);
        EmergencyEvent emergencyEvent = emergencyEventRepository.findOneByEmergencyEventId(warnListEmergencyRequestDto.getEmergencyEventId())
                .orElseThrow(() -> {
                    log.info("해당 emergencyEventId가 존재하지 않음.");
                    return new IllegalArgumentException("해당 emergencyEventId가 존재하지 않습니다.");
                });

        Map<Long, WarnInfo> result = new HashMap<Long, WarnInfo>();
        List<WarnRecord> warnRecords = warnRecordRepository.findAllByTimeAfterAndEmergencyEvent(LocalDateTime.ofInstant(Instant.ofEpochMilli(warnListEmergencyRequestDto.getTimeAfter()), ZoneId.systemDefault()), emergencyEvent);
        return getWarnInfos(result, warnRecords);
    }

    private List<WarnInfo> getWarnInfos(Map<Long, WarnInfo> warnInfoMap, List<WarnRecord> warnRecords) {
        for (WarnRecord v : warnRecords) {
            if (warnInfoMap.containsKey(v.getWarnRecordId().getCheckPointIndex())) {
                WarnInfo warnInfo = warnInfoMap.get(v.getWarnRecordId().getCheckPointIndex());
                warnInfo.getSessionIds().add(v.getWarnRecordId().getSessionId());
                warnInfoMap.put(v.getWarnRecordId().getCheckPointIndex(), warnInfo);
            } else {
                warnInfoMap.put(v.getWarnRecordId().getCheckPointIndex(), new WarnInfo(v.getWarnRecordId().getCheckPointIndex(), v.getCreatedDate(), new ArrayList<String>(Collections.singleton(v.getWarnRecordId().getSessionId()))));
            }
        }
        return warnInfoMap.values().stream().sorted(Comparator.comparing(WarnInfo::getCheckPointIndex)).toList();
    }

    // 시작 시간을 기준으로 어떤 차량이 경고를 받았는지 조회할 때
    public List<WarnInfo> getWarmListWithTimeAfter(String email, Long timeAfter) {
        validateAdminRole(email);
        Map<Long, WarnInfo> result = new HashMap<Long, WarnInfo>();
        List<WarnRecord> warnRecords = warnRecordRepository.findAllAfterTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(timeAfter), ZoneId.systemDefault()));
        return getWarnInfos(result, warnRecords);
    }

    private void validateAdminRole(String email) {
        Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("해당 email 의 유저가 존재하지 않음.");
                    return new IllegalArgumentException("해당 email 의 유저가 존재하지 않습니다.");
                });

        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한이 없습니다.");
            throw new IllegalArgumentException("관리자 권한이 없습니다.");
        }
    }
}
