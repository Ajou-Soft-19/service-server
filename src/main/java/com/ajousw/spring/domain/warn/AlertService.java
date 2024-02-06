package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.navigation.dto.AlertDto;
import com.ajousw.spring.domain.navigation.dto.BroadcastDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.BatchInsertJdbcRepository;
import com.ajousw.spring.domain.navigation.route.OsrmTableService;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleType;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.domain.warn.entity.EmergencyEvent;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.repository.EmergencyEventRepository;
import com.ajousw.spring.domain.warn.entity.repository.WarnRecordRepository;
import com.ajousw.spring.domain.warn.pubsub.RedisMessagePublisher;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final BatchInsertJdbcRepository batchInsertJdbcRepository;
    private final EmergencyEventRepository emergencyEventRepository;
    private final VehicleStatusRepository vehicleStatusRepository;
    private final RedisMessagePublisher redisMessagePublisher;
    private final WarnRecordRepository warnRecordRepository;
    private final OsrmTableService osrmTableService;

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
        long startTime = System.currentTimeMillis();
        log.info("Warning Start");
        String uuid = UUID.randomUUID().toString();
        log.info("<{}> Alert Request of {} with pathId {}", uuid, licenceNumber, emergencyPath.getNaviPathId());
        log.info("<{}> warning checkPointIdx {}", uuid, nextCheckPoint.getPointIndex());

        Optional<EmergencyEvent> eventOpt = emergencyEventRepository.findByNavigationPath(emergencyPath);
        if (eventOpt.isEmpty()) {
            log.info("No Such Emergency Event for NaviPathId {}", emergencyPath.getNaviPathId());
            return;
        }

        EmergencyEvent emergencyEvent = eventOpt.get();
        List<String> sessionIdAlreadyWarned = warnRecordRepository.findSessionIdByEmergencyEventIdAndCheckPointIndex(
                emergencyEventId, nextCheckPoint.getPointIndex());

        // 모든 차량
//        List<VehicleStatus> targetVehicleStatus = vehicleStatusRepository.findAll();

        // 필터링 차량
        List<VehicleStatus> targetVehicleStatus = filterTargetSession(nextCheckPoint, filteredPathPoints, duration)
                .stream()
                .filter(vehicleStatus -> !sessionIdAlreadyWarned.contains(vehicleStatus.getVehicleStatusId()))
                .toList();

        log.info("sessionId {}", sessionIdAlreadyWarned);

        Set<String> targetSession = targetVehicleStatus.stream()
                .map(VehicleStatus::getVehicleStatusId)
                .collect(Collectors.toSet());

        AlertDto alertDto = new AlertDto(emergencyEventId, nextCheckPoint.getPointIndex(), licenceNumber, vehicleType,
                emergencyPath.getCurrentPathPoint(), filteredPathPoints);

        redisMessagePublisher.publishAlertMessageToSocket(new BroadcastDto(targetSession, alertDto));
        addWarnRecord(uuid, emergencyEvent, nextCheckPoint.getPointIndex(), targetVehicleStatus);
        log.info("<{}> Alert Broadcast to {} vehicles", uuid, targetVehicleStatus.size());
        log.info("Warn ended {}ms", System.currentTimeMillis() - startTime);
    }

    /**
     * @param nextCheckPoint 다음 체크포인트
     * @param duration       응급 차량이 다음 체크포인트에 도달하기 까지의 시간
     * @return 1. 네비게이션 사용 중인 차량 -> 포함 2. 네비게이션 사용 중이지 않은 차량 -> 시간 내에 도달 가능한 차량만 포함 (거리상으론 가깝지만, 도로 상으론 멀리 떨어진 차량 필터링)
     */
    List<VehicleStatus> filterTargetSession(CheckPoint nextCheckPoint, List<PathPointDto> filteredPathPoints,
                                            double duration) {
        List<VehicleStatus> vehicleStatusInRange = vehicleStatusRepository.findAllWithinRadius(
                nextCheckPoint.getCoordinate().getX(), nextCheckPoint.getCoordinate().getY(), checkPointRadius);

        // 네비게이션을 사용하는 차량 포함
        List<VehicleStatus> vehicleUsingNavi = vehicleStatusInRange.stream()
                .filter(VehicleStatus::isUsingNavi)
                .toList();

        List<VehicleStatus> vehicleNotUsingNavi = vehicleStatusInRange.stream()
                .filter(vs -> !vs.isUsingNavi()).toList();

        List<VehicleStatus> targetVehicleStatus = new ArrayList<>(vehicleUsingNavi);

        if (vehicleNotUsingNavi.size() == 0) {
            return List.of();
        }

        List<String> sources = new ArrayList<>();
        for (VehicleStatus vehicleStatus : vehicleNotUsingNavi) {
            sources.add(coordinateToString(vehicleStatus.getCoordinate()));
        }
        String destination = coordinateToString(nextCheckPoint.getCoordinate());

        List<TableQueryResultDto> resultDtos;
        try {
            resultDtos = osrmTableService.getTableOfMultiSourceDistanceAndDuration(
                    sources, destination);
            if (sources.size() != resultDtos.size()) {
                throw new RuntimeException("table result 갯수 안 맞음");
            }
        } catch (Exception e) {
            log.error("table api 사용 중 오류 발생 {}", e.getMessage());
            targetVehicleStatus.addAll(vehicleNotUsingNavi);
            return targetVehicleStatus;
        }

        for (int i = 0; i < resultDtos.size(); i++) {
            TableQueryResultDto resultDto = resultDtos.get(i);
            if (resultDto.getDuration() <= duration) {
                targetVehicleStatus.add(vehicleNotUsingNavi.get(i));
            }
        }

        return targetVehicleStatus;
    }

    public void addWarnRecord(String uuid, EmergencyEvent emergencyEvent, Long checkPointIndex,
                              List<VehicleStatus> vehicleStatuses) {
        List<WarnRecord> newRecords = vehicleStatuses.stream()
                .map(vs -> new WarnRecord(emergencyEvent, checkPointIndex, vs))
                .toList();

        batchInsertJdbcRepository.saveAllWarnRecordsInBatch(newRecords);
        log.info("<{}> batchInsert Success", uuid);
    }

    private String coordinateToString(Point point) {
        return point.getX() + "," + point.getY();
    }

}
