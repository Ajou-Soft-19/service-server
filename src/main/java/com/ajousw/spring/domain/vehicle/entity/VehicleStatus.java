package com.ajousw.spring.domain.vehicle.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID vehicleStatusId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicleId")
    private Vehicle vehicle;

    private boolean usingNavi;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point coordinate;

    private int kmPerHour;

    private int direction;

    private LocalDateTime lastUpdateTime;

    @Builder
    public VehicleStatus(Vehicle vehicle, boolean usingNavi, Point coordinate, int kmPerHour,
                         int direction, LocalDateTime lastUpdateTime) {
        this.vehicle = vehicle;
        this.usingNavi = usingNavi;
        this.coordinate = coordinate;
        this.kmPerHour = kmPerHour;
        this.direction = direction;
        this.lastUpdateTime = lastUpdateTime;
    }

    public void modifyStatus(boolean usingNavi, Point coordinate, int kmPerHour,
                             int direction, LocalDateTime lastUpdateTime) {
        this.usingNavi = usingNavi;
        this.coordinate = coordinate;
        this.kmPerHour = kmPerHour;
        this.direction = direction;
        this.lastUpdateTime = lastUpdateTime;
    }
}
