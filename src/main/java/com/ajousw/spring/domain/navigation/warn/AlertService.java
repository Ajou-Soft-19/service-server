package com.ajousw.spring.domain.navigation.warn;

import com.ajousw.spring.domain.exception.BadApiResponseException;
import com.ajousw.spring.domain.navigation.dto.AlertDto;
import com.ajousw.spring.domain.navigation.dto.BroadcastDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.navigation.entity.PathPointRepository;
import com.ajousw.spring.domain.util.CoordinateUtil;
import com.ajousw.spring.domain.vehicle.VehicleType;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatusRepository;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final VehicleStatusRepository vehicleStatusRepository;
    private final PathPointRepository pathPointRepository;
    private WebClient webClient = WebClient.builder().build();

    @Value("${emergency.socket-server}")
    private String socketServerUrl;

    @Value("${emergency.filter-radius}")
    private Double filterRadius;

    //    @Value("${tmp.token}")
    private String tmpToken =
            "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqc201MzE1QGdtYWlsLmNvbSIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfRU1FUkdFTkNZX1ZFSElD"
                    + "TEUsUk9MRV9VU0VSIiwidXNlcm5hbWUiOiLsoJXshKDrrLgiLCJ0b2tlbklkIjoiZmYxOTFjMjAtMDgyMi00MTdlLTg2MWEtM2RjMzUxMGFjOTE1IiwiZXhwIjoxNz"
                    + "A1OTA4MDY5fQ.8pjs74VfYBkR9jSOjG1p3DKd54eImoNRC5Vyn2MXjq1DaMNYg6niJn2MOnT_LzKH7g5dw-jMiEItObp7pFIAkw";


    // TODO: Function X 구현
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alertNextCheckPoint(NavigationPath emergencyPath, List<PathPointDto> filteredPathPoints,
                                    CheckPoint nextCheckPoint, String licenceNumber, VehicleType vehicleType) {
        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAllWithinRadius(
                nextCheckPoint.getCoordinate().getY(), nextCheckPoint.getCoordinate().getX(), filterRadius);

//        // 나중엔 구분해서 처리 현재는 그냥 주변에 있는 차들에게 알림
//        List<VehicleStatus> vehicleUsingNavi = targetVehicleStatus.stream()
//                .filter(VehicleStatus::isUsingNavi).toList();
//        List<VehicleStatus> vehicleNotUsingNavi = targetVehicleStatus.stream()
//                .filter(vs -> !vs.isUsingNavi()).toList();
        log.info("{}", targetVehicleStatus);

        Set<String> targetSession = targetVehicleStatus.stream().map(VehicleStatus::getVehicleStatusId).collect(
                Collectors.toSet());

        sendAlertMessageBySocket(new AlertDto(licenceNumber, vehicleType,
                emergencyPath.getCurrentPathPoint(), filteredPathPoints), targetSession);
    }

    private void sendAlertMessageBySocket(AlertDto alertDto, Set<String> targetSession) {
        Map<String, Object> data = Map.of("info", alertDto, "target", targetSession);
        new BroadcastDto(targetSession, alertDto);
        try {
            webClient.post()
                    .uri(socketServerUrl + "/api/broadcast")
                    .header("Authorization", "Bearer " + tmpToken)
                    .bodyValue(data)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();

            if (statusCode.isError()) {
                log.error("Socket Broadcast api {} error", e.getStatusCode(), e);
                throw new BadApiResponseException("브로드캐스트 API 서버에 오류가 발생했습니다.");
            }
        }
    }

    private boolean filterPathInCheckPoint(CheckPoint checkPoint, PathPoint pathPoint) {
        MapLocation checkPointLocation
                = new MapLocation(checkPoint.getCoordinate().getY(), checkPoint.getCoordinate().getX());
        MapLocation pathPointLocation
                = new MapLocation(pathPoint.getCoordinate().getY(), pathPoint.getCoordinate().getX());
        double distance = CoordinateUtil.calculateDistance(checkPointLocation, pathPointLocation);

        return distance <= filterRadius;
    }

}
