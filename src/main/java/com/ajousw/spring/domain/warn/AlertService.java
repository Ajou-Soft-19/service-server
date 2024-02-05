package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.navigation.dto.AlertDto;
import com.ajousw.spring.domain.navigation.dto.BroadcastDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleType;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.domain.warn.entity.EmergencyEvent;
import com.ajousw.spring.domain.warn.entity.repository.EmergencyEventRepository;
import com.ajousw.spring.domain.warn.pubsub.RedisMessagePublisher;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final EmergencyEventRepository emergencyEventRepository;
    private final VehicleStatusRepository vehicleStatusRepository;
    private final RedisMessagePublisher redisMessagePublisher;
    private final EmergencyEventService emergencyEventService;

    @Value("${emergency.check-point-distance}")
    private double checkPointDistance;

    @Value("${emergency.check-point-radius}")
    private double checkPointRadius;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alertNextCheckPoint(NavigationPath emergencyPath, Long emergencyEventId,
                                    List<PathPointDto> filteredPathPoints,
                                    CheckPoint nextCheckPoint, double duration, String licenceNumber,
                                    VehicleType vehicleType) {

        String uuid = UUID.randomUUID().toString();
        log.info("<{}> Alert Request of {} with pathId {}", uuid, licenceNumber, emergencyPath.getNaviPathId());
        log.info("<{}> warning checkPointIdx {}", uuid, nextCheckPoint.getPointIndex());

        Optional<EmergencyEvent> eventOpt = emergencyEventRepository.findByNavigationPath(emergencyPath);
        if (eventOpt.isEmpty()) {
            log.info("No Such Emergency Event for NaviPathId {}", emergencyPath.getNaviPathId());
            return;
        }

        EmergencyEvent emergencyEvent = eventOpt.get();

        // 일단 모든 차량을 대상으로 알림
        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAll();
        Set<String> targetSession = targetVehicleStatus.stream().map(VehicleStatus::getVehicleStatusId).collect(
                Collectors.toSet());

        AlertDto alertDto = new AlertDto(emergencyEventId, nextCheckPoint.getPointIndex(), licenceNumber, vehicleType,
                emergencyPath.getCurrentPathPoint(), filteredPathPoints);

        redisMessagePublisher.publishAlertMessageToSocket(new BroadcastDto(targetSession, alertDto));
        emergencyEventService.addWarnRecord(uuid, emergencyEvent, nextCheckPoint.getPointIndex(), targetVehicleStatus);
        log.info("<{}> Alert Broadcast to {} vehicles", uuid, targetVehicleStatus.size());
    }

    Set<String> filterTargetSession(NavigationPath emergencyPath, CheckPoint nextCheckPoint, double duration) {
        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAllWithinRadius(
                nextCheckPoint.getCoordinate().getY(), nextCheckPoint.getCoordinate().getX(), checkPointRadius);

        // 네비게이션을 사용하는 차량 포함
        List<String> vehicleUsingNavi = targetVehicleStatus.stream()
                .filter(VehicleStatus::isUsingNavi)
                .map(VehicleStatus::getVehicleStatusId)
                .toList();
        Set<String> targetSession = new HashSet<>(vehicleUsingNavi);

        // TODO: 네비게이션을 사용하지 않는 차량 로직 구현
        List<VehicleStatus> vehicleNotUsingNavi = targetVehicleStatus.stream()
                .filter(vs -> !vs.isUsingNavi()).toList();
        targetSession.addAll(vehicleNotUsingNavi.stream().map(VehicleStatus::getVehicleStatusId).toList());

        return targetSession;
    }

}
