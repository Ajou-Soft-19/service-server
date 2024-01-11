package com.ajousw.spring.domain.navigation.dto;

import com.ajousw.spring.domain.navigation.api.Provider;
import com.ajousw.spring.domain.navigation.route.entity.MapLocation;
import com.ajousw.spring.domain.navigation.route.entity.NavigationPath;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NavigationPathDto {
    private Long naviPathId;
    private Long vehicleId;
    private Provider provider;
    private MapLocation sourceLocation;
    private MapLocation destLocation;
    private String queryType;
    private Long distance;
    private Long duration;
    private Long currentPathPoint;
    private List<PathPointDto> pathPoint;
    private List<PathGuideDto> pathGuide;

    public NavigationPathDto(NavigationPath navigationPath, List<PathPointDto> pathPoints,
                             List<PathGuideDto> pathGuides) {
        this.naviPathId = navigationPath.getNaviPathId();
//        this.vehicleId = navigationPath.getVehicle().getVehicleId();
        this.provider = navigationPath.getProvider();
        this.sourceLocation = navigationPath.getSourceLocation();
        this.destLocation = navigationPath.getDestLocation();
        this.queryType = navigationPath.getQueryType();
        this.distance = navigationPath.getDistance();
        this.duration = navigationPath.getDuration();
        this.currentPathPoint = navigationPath.getCurrentPathPoint();
        this.pathGuide = pathGuides;
        this.pathPoint = pathPoints;
    }

}
