package com.ajousw.spring.web.controller.dto.vehicleStatus;

import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleStatusDto {
    private String vehicleStatusId;
    private Double latitude;
    private Double longitude;
    private boolean isEmergencyVehicle;
    private LocalDateTime lastUpdateTime;
    private Double meterPerSec;
    private Double direction;


    // vehicle info
    private VehicleDto vehicleInfo;

    @Builder
    public VehicleStatusDto(VehicleStatus vehicleStatus) {
        if (vehicleStatus.getVehicle() != null) {
            this.vehicleInfo = new VehicleDto(vehicleStatus.getVehicle());
        }
        this.vehicleStatusId = vehicleStatus.getVehicleStatusId();
        this.latitude = vehicleStatus.getCoordinate().getY();
        this.longitude = vehicleStatus.getCoordinate().getX();
        this.isEmergencyVehicle = vehicleStatus.isEmergencyVehicle();
        this.lastUpdateTime = vehicleStatus.getLastUpdateTime();
        this.meterPerSec = vehicleStatus.getMeterPerSec();
        this.direction = vehicleStatus.getDirection();
    }

}
