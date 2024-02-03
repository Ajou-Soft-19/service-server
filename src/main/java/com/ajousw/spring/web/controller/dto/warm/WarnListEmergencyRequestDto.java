package com.ajousw.spring.web.controller.dto.warm;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WarnListEmergencyRequestDto {
//    private Long timeAfter;
    private Long emergencyEventId;
    private Long checkPointIndex;
}
