package com.ajousw.spring.domain.vehicle.entity;

import com.ajousw.spring.domain.navigation.route.entity.MapLocation;
import jakarta.persistence.Embedded;
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

    @Embedded
    private MapLocation mapLocation;

    private int kmPerHour;

    private int direction;

    private LocalDateTime lastUpdateTime;

    @Builder
    public VehicleStatus(Vehicle vehicle, boolean usingNavi, double latitude, double longitude, int kmPerHour,
                         int direction, LocalDateTime lastUpdateTime) {
        this.vehicle = vehicle;
        this.usingNavi = usingNavi;
        this.mapLocation = new MapLocation(latitude, longitude);
        this.kmPerHour = kmPerHour;
        this.direction = direction;
        this.lastUpdateTime = lastUpdateTime;
    }

    public void modifyStatus(boolean usingNavi, double latitude, double longitude, int kmPerHour,
                             int direction, LocalDateTime lastUpdateTime) {
        this.usingNavi = usingNavi;
        this.mapLocation = new MapLocation(latitude, longitude);
        this.kmPerHour = kmPerHour;
        this.direction = direction;
        this.lastUpdateTime = lastUpdateTime;
    }
}
