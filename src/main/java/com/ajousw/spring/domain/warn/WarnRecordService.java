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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class WarnRecordService {
    private final WarnRecordRepository warnRecordRepository;
    private final EmergencyEventRepository emergencyEventRepository;
    private final MemberJpaRepository memberJpaRepository;

    // Long to LocalDateTime
    private LocalDateTime longToLocalDateTime(Long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public List<WarnInfo> getWarnList(String email, WarnListEmergencyRequestDto warnListEmergencyRequestDto) {
        validateAdminRole(email);
        if (warnListEmergencyRequestDto.getCheckPointIndex() == null
                && warnListEmergencyRequestDto.getEmergencyEventId() == null) {
            return getWarnList();
        } else if (warnListEmergencyRequestDto.getCheckPointIndex() == null) {
            return getWarnList(warnListEmergencyRequestDto.getEmergencyEventId());
        } else {
            return getWarnList(warnListEmergencyRequestDto.getEmergencyEventId(),
                    warnListEmergencyRequestDto.getCheckPointIndex());
        }
    }

    // getWarnList with time
//    public List<WarnInfo> getWarnList(Long timeAfter) {
//        // 시작 시간을 기준으로 어떤 차량이 경고를 받았는지 조회할 때
//        List<WarnRecord> warnRecords = warnRecordRepository.findAllAfterTime(longToLocalDateTime(timeAfter));
//        return getWarnInfos(warnRecords);
//    }

    // getWarnList with time and emergencyEventId
    public List<WarnInfo> getWarnList(Long emergencyEventId) {
        isEmergencyEventById(emergencyEventId);
        List<WarnRecord> warnRecords = warnRecordRepository.findAllByEmergencyEventId(emergencyEventId);
        return getWarnInfos(warnRecords);
    }

    // 조건 없이 getWarnList 조회
    public List<WarnInfo> getWarnList() {
        List<WarnRecord> warnRecords = warnRecordRepository.findAll();
        return getWarnInfos(warnRecords);
    }

    // getWarnList with time and emergencyEventId and checkPointIndex
    public List<WarnInfo> getWarnList(Long emergencyEventId, Long checkPointIndex) {
        isEmergencyEventById(emergencyEventId);
        List<WarnRecord> result = warnRecordRepository.findAllByEmergencyEventIdAndCheckPointIndex(emergencyEventId,
                checkPointIndex);
        return getWarnInfos(result);
    }

    private void isEmergencyEventById(Long emergencyEventId) {
        if (!emergencyEventRepository.existsByEmergencyEventId(emergencyEventId)) {
            log.info("Not Exist EmergencyEvent");
            throw new IllegalArgumentException("Not Exist EmergencyEvent");
        }
    }

    // emergencyEventId로 EmergencyEvent 조회
    private EmergencyEvent getEmergencyEventById(Long emergencyEventId) {
        return emergencyEventRepository.findOneByEmergencyEventId(emergencyEventId)
                .orElseThrow(() -> {
                    log.info("해당 emergencyEventId가 존재하지 않음.");
                    return new IllegalArgumentException("해당 emergencyEventId가 존재하지 않습니다.");
                });
    }

    private List<WarnInfo> getWarnInfos(List<WarnRecord> warnRecords) {
        Map<Long, WarnInfo> warnInfoMap = new HashMap<Long, WarnInfo>();
        for (WarnRecord v : warnRecords) {
            if (warnInfoMap.containsKey(v.getWarnRecordId().getCheckPointIndex())) {
                WarnInfo warnInfo = warnInfoMap.get(v.getWarnRecordId().getCheckPointIndex());
                warnInfo.getSessionIds().add(v.getWarnRecordId().getSessionId());
                warnInfoMap.put(v.getWarnRecordId().getCheckPointIndex(), warnInfo);
            } else {
                warnInfoMap.put(v.getWarnRecordId().getCheckPointIndex(),
                        new WarnInfo(v.getWarnRecordId().getCheckPointIndex(), v.getCreatedDate(),
                                new ArrayList<String>(Collections.singleton(v.getWarnRecordId().getSessionId())),
                                v.getEmergencyEvent().getEmergencyEventId()));
            }
        }
        return warnInfoMap.values().stream().sorted(Comparator.comparing(WarnInfo::getCheckPointIndex)).toList();
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
