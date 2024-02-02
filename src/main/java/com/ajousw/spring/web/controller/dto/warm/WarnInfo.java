package com.ajousw.spring.web.controller.dto.warm;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarnInfo {
    public WarnInfo(Long checkPointIndex, LocalDateTime warnCreateTime, List<String> sessionIds) {
        this.checkPointIndex = checkPointIndex;
        this.warnCreateTime = warnCreateTime;
        this.sessionIds = sessionIds;
    }

    private Long checkPointIndex; // 경고 대상 체크 포인트
    private LocalDateTime warnCreateTime; // 경고가 발생된 시간
    private List<String> sessionIds; // 경고를 받은 대상 차량
}
