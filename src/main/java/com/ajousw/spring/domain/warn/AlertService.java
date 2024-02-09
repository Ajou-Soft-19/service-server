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

    @Value("${emergency.danger-zone-distance}")
    private double dangerZoneDistance;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void alertNextCheckPoint(NavigationPath emergencyPath, Long emergencyEventId,
                                    List<PathPointDto> filteredPathPoints,
                                    CheckPoint nextCheckPoint, Point currentPoint, double duration,
                                    String licenceNumber,
                                    VehicleType vehicleType) {
        long startTime = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString();
        log.info("<{}> Alert Request of {} with pathId {}", uuid, licenceNumber, emergencyPath.getNaviPathId());
        log.info("<{}> warning checkPointIdx {}", uuid, nextCheckPoint.getPointIndex());

        Optional<EmergencyEvent> eventOpt = emergencyEventRepository.findByNavigationPath(emergencyPath);
        if (eventOpt.isEmpty()) {
            log.info("No Such Emergency Event for NaviPathId {}", emergencyPath.getNaviPathId());
            return;
        }

        EmergencyEvent emergencyEvent = eventOpt.get();
        List<VehicleStatus> targetVehicleStatus = filterVehicleStatusToBroadCast(
                emergencyEventId, currentPoint, nextCheckPoint, duration);

        broadCastAlert(emergencyPath, emergencyEventId, filteredPathPoints, nextCheckPoint, licenceNumber, vehicleType,
                uuid, targetVehicleStatus);
        addWarnRecord(uuid, emergencyEvent, nextCheckPoint.getPointIndex(), targetVehicleStatus);
        log.info("<{}> Warn ended {}ms", uuid, System.currentTimeMillis() - startTime);
    }

    private List<VehicleStatus> filterVehicleStatusToBroadCast(Long emergencyEventId,
                                                               Point currentPoint,
                                                               CheckPoint nextCheckPoint, double duration) {
        List<String> sessionIdAlreadyWarned = warnRecordRepository.findSessionIdByEmergencyEventIdAndCheckPointIndex(
                emergencyEventId, nextCheckPoint.getPointIndex());

        return filterTargetSession(nextCheckPoint, currentPoint, duration)
                .stream()
                .filter(vehicleStatus -> !sessionIdAlreadyWarned.contains(vehicleStatus.getVehicleStatusId()))
                .toList();
    }

    private void broadCastAlert(NavigationPath emergencyPath, Long emergencyEventId,
                                List<PathPointDto> filteredPathPoints,
                                CheckPoint nextCheckPoint, String licenceNumber, VehicleType vehicleType, String uuid,
                                List<VehicleStatus> targetVehicleStatus) {
        Set<String> targetSession = targetVehicleStatus.stream()
                .map(VehicleStatus::getVehicleStatusId)
                .collect(Collectors.toSet());
        AlertDto alertDto = new AlertDto(emergencyEventId, nextCheckPoint.getPointIndex(), licenceNumber, vehicleType,
                emergencyPath.getCurrentPathPoint(), filteredPathPoints);

        redisMessagePublisher.publishAlertMessageToSocket(new BroadcastDto(targetSession, alertDto));
        log.info("<{}> Alert Broadcast to {} vehicles", uuid, targetVehicleStatus.size());
    }


    public void addWarnRecord(String uuid, EmergencyEvent emergencyEvent, Long checkPointIndex,
                              List<VehicleStatus> vehicleStatuses) {
        if (vehicleStatuses.size() == 0) {
            return;
        }

        List<WarnRecord> newRecords = vehicleStatuses.stream()
                .map(vs -> new WarnRecord(emergencyEvent, checkPointIndex, vs))
                .toList();

        batchInsertJdbcRepository.saveAllWarnRecordsInBatch(newRecords);
        log.info("<{}> batchInsert Success", uuid);
    }

    /**
     * @param nextCheckPoint 다음 체크포인트
     * @param duration       응급 차량이 다음 체크포인트에 도달하기 까지의 시간
     * @return 1. 네비게이션 사용 중인 차량 -> 포함 2. 네비게이션 사용 중이지 않은 차량 -> 시간 내에 도달 가능한 차량만 포함 (거리상으론 가깝지만, 도로 상으론 멀리 떨어진 차량 필터링)
     */
    List<VehicleStatus> filterTargetSession(CheckPoint nextCheckPoint, Point currentPoint, double duration) {
        List<VehicleStatus> vehicleStatusInRange = vehicleStatusRepository.findAllWithinRadius(
                nextCheckPoint.getCoordinate().getX(), nextCheckPoint.getCoordinate().getY(), checkPointRadius);

        // 네비게이션을 사용하는 차량 포함
        List<VehicleStatus> vehicleUsingNavi = vehicleStatusInRange.stream()
                .filter(VehicleStatus::isUsingNavi)
                .toList();
        List<VehicleStatus> targetVehicleStatus = new ArrayList<>(vehicleUsingNavi);

        List<VehicleStatus> vehicleNotUsingNavi = vehicleStatusInRange.stream()
                .filter(vs -> !vs.isUsingNavi())
                .toList();
        if (vehicleNotUsingNavi.size() == 0) {
            return targetVehicleStatus;
        }

        findAndAddVehiclesInCheckPointRadius(nextCheckPoint, duration, vehicleNotUsingNavi, targetVehicleStatus);
        findAndAddVehiclesInDangerZone(currentPoint, vehicleNotUsingNavi, targetVehicleStatus);

        return targetVehicleStatus;
    }

    private void findAndAddVehiclesInCheckPointRadius(CheckPoint nextCheckPoint, double duration,
                                                      List<VehicleStatus> vehicleNotUsingNavi,
                                                      List<VehicleStatus> targetVehicleStatus) {
        List<String> sources = getSources(vehicleNotUsingNavi);
        List<Double> directions = getDirections(vehicleNotUsingNavi);
        String destination = pointToString(nextCheckPoint.getCoordinate());

        try {
            List<TableQueryResultDto> resultDtos = osrmTableService.getTableOfMultiSourceDistanceAndDurationWithHeading(
                    sources, destination, directions);
            checkResultSize(sources, resultDtos);
            addVehiclesInCheckPoint(duration, vehicleNotUsingNavi, targetVehicleStatus, resultDtos);
        } catch (Exception e) {
            handleApiError(vehicleNotUsingNavi, targetVehicleStatus, e, true);
        }
    }

    private void addVehiclesInCheckPoint(double duration, List<VehicleStatus> vehicleNotUsingNavi,
                                         List<VehicleStatus> targetVehicleStatus,
                                         List<TableQueryResultDto> resultDtos) {
        for (int i = 0; i < resultDtos.size(); i++) {
            TableQueryResultDto resultDto = resultDtos.get(i);
            if (resultDto.getDuration() <= duration) {
                targetVehicleStatus.add(vehicleNotUsingNavi.get(i));
            }
        }
    }

    private void findAndAddVehiclesInDangerZone(Point currentPoint, List<VehicleStatus> vehicleNotUsingNavi,
                                                List<VehicleStatus> targetVehicleStatus) {
        List<VehicleStatus> vehiclesNotInTargetStatus = getVehiclesNotInTargetStatus(vehicleNotUsingNavi,
                targetVehicleStatus);
        log.info("Target Vehicle size {}", vehiclesNotInTargetStatus.size());

        if (vehiclesNotInTargetStatus.size() == 0) {
            return;
        }

        List<String> sources = getSources(vehiclesNotInTargetStatus);
        String destination = pointToString(currentPoint);

        try {
            List<TableQueryResultDto> resultDtos = osrmTableService.getTableOfMultiSourceDistanceAndDuration(
                    sources, destination);
            checkResultSize(sources, resultDtos);
            addVehiclesInDangerZone(dangerZoneDistance, vehiclesNotInTargetStatus, targetVehicleStatus, resultDtos);
        } catch (Exception e) {
            handleApiError(vehicleNotUsingNavi, targetVehicleStatus, e, false);
        }
    }

    private void addVehiclesInDangerZone(double distance, List<VehicleStatus> vehiclesNotInTargetStatus,
                                         List<VehicleStatus> targetVehicleStatus,
                                         List<TableQueryResultDto> resultDtos) {
        for (int i = 0; i < resultDtos.size(); i++) {
            TableQueryResultDto resultDto = resultDtos.get(i);
            if (resultDto.getDistance() <= distance) {
                log.info("In Danger Zone");
                targetVehicleStatus.add(vehiclesNotInTargetStatus.get(i));
            }
        }
    }


    private List<VehicleStatus> getVehiclesNotInTargetStatus(List<VehicleStatus> vehicleNotUsingNavi,
                                                             List<VehicleStatus> targetVehicleStatus) {
        return vehicleNotUsingNavi.stream()
                .filter(vehicleStatus -> !targetVehicleStatus.contains(vehicleStatus))
                .collect(Collectors.toList());
    }

    private List<String> getSources(List<VehicleStatus> vehicleStatuses) {
        List<String> sources = new ArrayList<>();
        for (VehicleStatus vehicleStatus : vehicleStatuses) {
            sources.add(pointToString(vehicleStatus.getCoordinate()));
        }
        return sources;
    }

    private List<Double> getDirections(List<VehicleStatus> vehicleStatuses) {
        List<Double> directions = new ArrayList<>();
        for (VehicleStatus vehicleStatus : vehicleStatuses) {
            directions.add(vehicleStatus.getDirection());
        }
        return directions;
    }

    private void checkResultSize(List<String> sources, List<TableQueryResultDto> resultDtos) {
        if (sources.size() != resultDtos.size()) {
            throw new RuntimeException("table result 갯수 안 맞음");
        }
    }

    private void handleApiError(List<VehicleStatus> vehicleNotUsingNavi, List<VehicleStatus> targetVehicleStatus,
                                Exception e, boolean addAllOnError) {
        if (addAllOnError) {
            log.error("1차 필터링 table api 사용 중 오류 발생 {}", e.getMessage());
            targetVehicleStatus.addAll(vehicleNotUsingNavi);
        }

        log.error("2차 필터링 table api 사용 중 오류 발생 {}", e.getMessage());
    }

    private String pointToString(Point point) {
        return point.getX() + "," + point.getY();
    }

}
