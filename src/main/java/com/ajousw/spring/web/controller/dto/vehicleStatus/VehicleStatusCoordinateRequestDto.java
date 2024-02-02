package com.ajousw.spring.web.controller.dto.vehicleStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleStatusCoordinateRequestDto {
    private Double latitude;
    private Double longitude;
    private Double radius;
}
