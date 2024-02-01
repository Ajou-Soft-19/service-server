package com.ajousw.spring.web.controller.dto.vehicleStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleStatusCoordinateRequestDto {
    private Double latitude;
    private Double longitude;
    private Double radius;
}
