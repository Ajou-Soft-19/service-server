package com.ajousw.spring.web.controller.dto.navigationPath;

import com.ajousw.spring.domain.navigation.api.provider.factory.Provider;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NavigationPathWithPointsDto {
    private String vehicleStatusId;
    private Long vehicleId;
    private VehicleType vehicleType;
    private String licenceNumber;
    private Double longitude;
    private Double latitude;
    private Double direction;

    // navigationPath
    private Long navigationPathId;
    private Double destLatitude;
    private Double destLongitude;
    private Double sourceLatitude;
    private Double sourceLongitude;
    private Long currentPathPoint;
    private Long distance;
    private Long duration;
    private Provider provider;
    private Long pathPointSize;

    // pathPoints
    private List<PathPointItem> pathPoints;

    // checkPoints
    private List<CheckPointItem> checkPoints;

    public NavigationPathWithPointsDto(NavigationPath navigationPath,
                                       Vehicle vehicle,
                                       VehicleStatus vehicleStatus,
                                       List<PathPointItem> pathPoints,
                                       List<CheckPointItem> checkPoints) {
        this.vehicleId = vehicle.getVehicleId();
        this.vehicleStatusId = vehicleStatus.getVehicleStatusId();
        this.vehicleType = vehicle.getVehicleType();
        this.licenceNumber = vehicle.getLicenceNumber();
        this.direction = vehicleStatus.getDirection();

        // navigationPath
        this.navigationPathId = navigationPath.getNaviPathId();
        this.destLatitude = navigationPath.getDestLocation().getLatitude();
        this.destLongitude = navigationPath.getDestLocation().getLongitude();
        this.sourceLatitude = navigationPath.getSourceLocation().getLatitude();
        this.sourceLongitude = navigationPath.getSourceLocation().getLongitude();
        this.currentPathPoint = navigationPath.getCurrentPathPoint();
        this.distance = navigationPath.getDistance();
        this.duration = navigationPath.getDuration();
        this.provider = navigationPath.getProvider();
        this.pathPointSize = navigationPath.getPathPointSize();

        // pathPoints
        this.pathPoints = pathPoints;

        // checkPoints
        this.checkPoints = checkPoints;
    }
}
