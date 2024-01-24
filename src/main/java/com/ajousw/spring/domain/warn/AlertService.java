package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.navigation.dto.AlertDto;
import com.ajousw.spring.domain.navigation.dto.BroadcastDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.vehicle.VehicleType;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.domain.warn.pubsub.RedisMessagePublisher;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final VehicleStatusRepository vehicleStatusRepository;
    private final RedisMessagePublisher redisMessagePublisher;

    // TODO: Function X 구현
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alertNextCheckPoint(NavigationPath emergencyPath, List<PathPointDto> filteredPathPoints,
                                    CheckPoint nextCheckPoint, double duration, String licenceNumber,
                                    VehicleType vehicleType) {
//        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAllWithinRadius(
//                nextCheckPoint.getCoordinate().getY(), nextCheckPoint.getCoordinate().getX(), filterRadius);
        // 일단 모든 차량을 대상으로 알림
        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAll();

//
//        List<VehicleStatus> vehicleUsingNavi = targetVehicleStatus.stream()
//                .filter(VehicleStatus::isUsingNavi).toList();
//        List<VehicleStatus> vehicleNotUsingNavi = targetVehicleStatus.stream()
//                .filter(vs -> !vs.isUsingNavi()).toList();

        Set<String> targetSession = targetVehicleStatus.stream().map(VehicleStatus::getVehicleStatusId).collect(
                Collectors.toSet());

        AlertDto alertDto = new AlertDto(licenceNumber, vehicleType,
                emergencyPath.getCurrentPathPoint(), filteredPathPoints);

        redisMessagePublisher.publishAlertMessageToSocket(new BroadcastDto(targetSession, alertDto));
    }

}
