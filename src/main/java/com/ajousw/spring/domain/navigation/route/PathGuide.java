package com.ajousw.spring.domain.navigation.route;

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
public class PathGuide {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID pathGuideId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "navigation_path_id")
    private NavigationPath navigationPath;

    private Long pointIndex;

    private Long type;

    private Long distance;

    private Long duration;

    public PathGuide(NavigationPath navigationPath, Long pointIndex, Long type, Long distance, Long duration) {
        this.navigationPath = navigationPath;
        this.pointIndex = pointIndex;
        this.type = type;
        this.distance = distance;
        this.duration = duration;
    }
}
