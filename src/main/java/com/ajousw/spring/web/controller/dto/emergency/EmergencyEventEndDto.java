package com.ajousw.spring.web.controller.dto.emergency;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmergencyEventEndDto {

    private Long emergencyEventId;

}
