package com.ajousw.spring.web.controller.dto.vehicleStatus;

import com.ajousw.spring.domain.vehicle.VehicleType;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VehicleStatusDto {
    @Builder
    public VehicleStatusDto(VehicleStatus vehicleStatus) {
        // vehicle info
        this.vehicleId = vehicleStatus.getVehicle().getVehicleId();
        this.licenceNumber = vehicleStatus.getVehicle().getLicenceNumber();
        this.vehicleType = vehicleStatus.getVehicle().getVehicleType();
        this.vehicleStatusId = vehicleStatus.getVehicleStatusId();
        this.latitude = vehicleStatus.getCoordinate().getX();
        this.longitude = vehicleStatus.getCoordinate().getY();
        this.isEmergencyVehicle = vehicleStatus.isEmergencyVehicle();
        this.lastUpdateTime = vehicleStatus.getLastUpdateTime();
        this.meterPerSec = vehicleStatus.getMeterPerSec();
        this.direction = vehicleStatus.getDirection();

        // TODO: 수정해야 함.
//        this.isOnAction = true;
//        this.navigationPathId = navigationPath.getNaviPathId();
    }
    private String vehicleStatusId;
//    private UUID vehicleStatusId;
    private VehicleType vehicleType;
    private Double longitude;
    private Double latitude;
    private boolean isEmergencyVehicle;
    private LocalDateTime lastUpdateTime;
    private Double meterPerSec;
    private Double direction;
    private boolean isOnAction;
    private Long navigationPathId;

    // vehicle info
    private Long vehicleId;
    private String licenceNumber;
}
