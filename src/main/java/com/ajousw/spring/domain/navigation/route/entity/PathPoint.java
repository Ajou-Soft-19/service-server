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
public class PathPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID pathPointId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "navigation_path_id")
    private NavigationPath navigationPath;

    private Long index;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point coordinate;

    public PathPoint(NavigationPath navigationPath, Long index, Point coordinate) {
        this.navigationPath = navigationPath;
        this.index = index;
        this.coordinate = coordinate;
    }

}
