package com.ajousw.spring.domain.navigation.route.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CheckPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID checkPointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "navigation_path_id")
    private NavigationPath navigationPath;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point coordinate;

    private Long pointIndex;

    private Double distance;

    private Double duration;

    public CheckPoint(NavigationPath navigationPath, Point coordinate, Long pointIndex, Double distance,
                      Double duration) {
        this.navigationPath = navigationPath;
        this.coordinate = coordinate;
        this.pointIndex = pointIndex;
        this.distance = distance;
        this.duration = duration;
    }


    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }
}
