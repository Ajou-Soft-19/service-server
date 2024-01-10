package com.ajousw.spring.domain.navigation.api.info;

import java.util.Map;
import lombok.Data;

@Data
public class Guide {
    private final long pointIndex;
    private final int type;
    private final String instructions;
    private final long distance;
    private final long duration;

    public Guide(Map<String, Object> guideInfo) {
        this.pointIndex = ((Integer) guideInfo.get("pointIndex")).longValue();
        this.type = (int) guideInfo.get("type");
        this.instructions = (String) guideInfo.get("instructions");
        this.distance = ((Integer) guideInfo.get("distance")).longValue();
        this.duration = ((Integer) guideInfo.get("duration")).longValue();
    }
}