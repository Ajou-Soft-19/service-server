package com.ajousw.spring.domain.navigation.dto;

import com.ajousw.spring.domain.vehicle.VehicleType;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertDto {
    private String licenseNumber;

    private VehicleType vehicleType;

    private Long currentPathPoint;

    private List<PathPointDto> pathPoints;
}
