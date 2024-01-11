package com.ajousw.spring.domain.navigation.route.entity;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import com.ajousw.spring.domain.navigation.api.Provider;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Enumerated
    private Provider provider;

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

    private Long maxPathIndex;

    @Builder
    public NavigationPath(Member member, Vehicle vehicle, Provider provider, MapLocation sourceLocation, MapLocation destLocation,
                          String queryType, Long distance, Long duration, Long currentPathPoint, Long pathPointSize) {
        this.member = member;
        this.provider = provider;
        this.vehicle = vehicle;
        this.sourceLocation = sourceLocation;
        this.destLocation = destLocation;
        this.queryType = queryType;
        this.distance = distance;
        this.duration = duration;
        this.currentPathPoint = currentPathPoint;
        this.maxPathIndex = pathPointSize;
    }

    public void updateCurrentPathPoint(Long currentIdx) {
        if(currentIdx < 0 || this.maxPathIndex >= currentIdx) {
            throw new IllegalArgumentException("Wrong Index Range");
        }

        this.maxPathIndex = currentIdx;
    }
}
