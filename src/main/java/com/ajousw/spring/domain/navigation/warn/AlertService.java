package com.ajousw.spring.domain.navigation.warn;

import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.navigation.entity.PathPointRepository;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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


    // TODO: Function X 구현
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alertNextCheckPoint(NavigationPath emergencyPath, List<PathPoint> pathPoint,
                                    CheckPoint nextCheckPoint) {
        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAllWithinRadius(
                nextCheckPoint.getCoordinate().getY(), nextCheckPoint.getCoordinate().getX(), filterRadius);

        // 나중엔 구분해서 처리 현재는 그냥 주변에 있는 차들에게 알림
        List<VehicleStatus> vehicleUsingNavi = targetVehicleStatus.stream()
                .filter(VehicleStatus::isUsingNavi).toList();
        List<VehicleStatus> vehicleNotUsingNavi = targetVehicleStatus.stream()
                .filter(vs -> !vs.isUsingNavi()).toList();
        log.info("{}", targetVehicleStatus);
    }

}
