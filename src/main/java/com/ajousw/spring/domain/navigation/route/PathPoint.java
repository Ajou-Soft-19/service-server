package com.ajousw.spring.domain.navigation.route;

import jakarta.persistence.Embedded;
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

    @Embedded
    private MapLocation location;

    public PathPoint(NavigationPath navigationPath, Long index, double latitude, double longitude) {
        this.navigationPath = navigationPath;
        this.index = index;
        this.location = new MapLocation(latitude, longitude);
    }

}
