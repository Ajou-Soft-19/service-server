package com.ajousw.spring.domain.navigation.dto;

import com.ajousw.spring.domain.navigation.route.entity.MapLocation;
import com.ajousw.spring.domain.navigation.route.entity.PathPoint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathPointDto {
    private Long index;
    private MapLocation location;

    public PathPointDto(PathPoint pathPoint) {
        this.index = pathPoint.getIndex();
        this.location = pathPoint.getLocation();
    }


}
