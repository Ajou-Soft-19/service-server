package com.ajousw.spring.domain.util;

import com.ajousw.spring.domain.navigation.entity.MapLocation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class CoordinateUtilTest {

    @Test
    void calculateDistance() {
        double seoulLat = 37.5665;
        double seoulLon = 126.9780;
        double busanLat = 35.1796;
        double busanLon = 129.0756;

        double distance = CoordinateUtil.calculateDistance(new MapLocation(seoulLat, seoulLon),
                new MapLocation(busanLat, busanLon));

        log.info("{}", distance * 1000);
    }
}