package com.ajousw.spring.domain.warn.util;

import com.ajousw.spring.domain.navigation.entity.MapLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoordinateUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    // 서울에서 부산까지 계산 시 오차 약 300m
    public static double calculateDistance(MapLocation source, MapLocation dest) {
        double lat1 = source.getLatitude();
        double lon1 = source.getLongitude();
        double lat2 = dest.getLatitude();
        double lon2 = dest.getLongitude();

        // 위도와 경도를 라디안으로 변환
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine 공식
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c * 1000;
    }
}
