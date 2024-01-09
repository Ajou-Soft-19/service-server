package com.ajousw.spring.domain.navigation.route;

import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import com.ajousw.spring.domain.vehicle.Vehicle;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NavigationPath extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long naviPathId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "source_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "source_longitude"))
    })
    private MapLocation sourceLocation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "dest_latitude")),
            @AttributeOverride(name = "longitude", column = @Column(name = "dest_longitude"))
    })
    private MapLocation destLocation;

    private String queryType;

    private Long distance;

    private Long duration;

    private Long currentPathPoint;

    @OneToMany(mappedBy = "navigationPath", fetch = FetchType.LAZY)
    private final List<PathPoint> pathPoints = new ArrayList<>();

    @OneToMany(mappedBy = "navigationPath", fetch = FetchType.LAZY)
    private final List<PathGuide> guides = new ArrayList<>();

    @Builder
    public NavigationPath(Vehicle vehicle, MapLocation sourceLocation, MapLocation destLocation, String queryType,
                          Long distance, Long duration, Long currentPathPoint) {
        this.vehicle = vehicle;
        this.sourceLocation = sourceLocation;
        this.destLocation = destLocation;
        this.queryType = queryType;
        this.distance = distance;
        this.duration = duration;
        this.currentPathPoint = currentPathPoint;
    }
}
