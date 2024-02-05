package com.ajousw.spring.web.controller.dto.vehicleStatus;

import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleStatusNavigationPathDto {
    public VehicleStatusNavigationPathDto(VehicleStatus vehicleStatus, NavigationPath navigationPath) {
        this.vehicleStatusId = vehicleStatus.getVehicleStatusId();
        this.vehicleId = vehicleStatus.getVehicle().getVehicleId();
        this.vehicleType = vehicleStatus.getVehicle().getVehicleType();
        this.licenceNumber = vehicleStatus.getVehicle().getLicenceNumber();
        this.latitude = vehicleStatus.getCoordinate().getX();
        this.longitude = vehicleStatus.getCoordinate().getY();
        this.isEmergencyVehicle = vehicleStatus.isEmergencyVehicle();
        this.lastUpdateTime = vehicleStatus.getLastUpdateTime();
        this.meterPerSec = vehicleStatus.getMeterPerSec();
        this.direction = vehicleStatus.getDirection();

        this.isOnAction = true;
        this.navigationPathId = navigationPath.getNaviPathId();
        this.currentPathPoint = navigationPath.getCurrentPathPoint();
    }

    //    private UUID vehicleStatusId;
    private String vehicleStatusId;
    private Long vehicleId;
    private VehicleType vehicleType;
    private String licenceNumber;
    private Double longitude;
    private Double latitude;
    private boolean isEmergencyVehicle;
    private LocalDateTime lastUpdateTime;
    private Double meterPerSec;
    private Double direction;
    private boolean isOnAction;
    private Long navigationPathId;
    private Long currentPathPoint;
}
