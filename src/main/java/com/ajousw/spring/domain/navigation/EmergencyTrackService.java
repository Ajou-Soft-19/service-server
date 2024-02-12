package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.api.OsrmTableService;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.warn.AlertService;
import com.ajousw.spring.domain.warn.util.CoordinateUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyTrackService {

    private final AlertService alertService;
    private final OsrmTableService osrmTableService;
    private final NavigationPathRepository navigationPathRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${emergency.check-point-radius}")
    private double checkPointRadius;

    // TODO: 캐싱을 통해 성능 향상 (2차 캐시 or Reids or ConcurrentHashMap으로 구현)
    public void updateCurrentPathPoint(Long naviPathId, Long emergencyEventId, Long curPathIdx,
                                       Double currentLongitude, Double currentLatitude) {
        NavigationPath navigationPath = findNavigationPathByIdFetchJoin(naviPathId);
        Long vehicleId = navigationPath.getVehicle().getVehicleId();
        navigationPath.updateCurrentPathPoint(curPathIdx);
        log.info("updated pathPoint for vehicleId {} naviPathId {} pathIndex {}",
                vehicleId, navigationPath.getNaviPathId(), curPathIdx);

        List<CheckPoint> checkPoints = navigationPath.getCheckPoints();
        Point currentPoint = geometryFactory.createPoint(
                new Coordinate(currentLongitude, currentLatitude));
        alertNextCheckPoint(navigationPath, emergencyEventId, vehicleId, curPathIdx, checkPoints, currentPoint);
    }

    private void alertNextCheckPoint(NavigationPath navigationPath, Long emergencyEventId, Long vehicleId,
                                     Long curPathIdx, List<CheckPoint> checkPoints, Point currentPoint) {
        Optional<CheckPoint> nextCheckPointOptional = findNextCheckPoint(curPathIdx,
                navigationPath.getCurrentCheckPoint(), checkPoints);

        if (nextCheckPointOptional.isEmpty()) {
            return;
        }

        CheckPoint nextCheckPoint = nextCheckPointOptional.get();
        navigationPath.updateCheckPoint(nextCheckPoint.getPointIndex());

        List<PathPointDto> filteredPathPoints = navigationPath.getPathPoints().stream()
                .filter(p -> filterPathInCheckPoint(curPathIdx, nextCheckPoint, p))
                .map(PathPointDto::new).toList();

        double duration = calculateDuration(currentPoint, nextCheckPoint);

        alertService.alertNextCheckPoint(navigationPath, emergencyEventId, vehicleId, filteredPathPoints,
                nextCheckPoint,
                currentPoint, duration, navigationPath.getVehicle().getLicenceNumber(),
                navigationPath.getVehicle().getVehicleType());
    }

    private Optional<CheckPoint> findNextCheckPoint(Long curPathIdx, Long currentCheckPointIdx,
                                                    List<CheckPoint> checkPoints) {
        if (curPathIdx < currentCheckPointIdx) {
            return checkPoints.stream()
                    .filter(c -> Objects.equals(c.getPointIndex(), currentCheckPointIdx))
                    .findFirst();
        }

        return checkPoints.stream()
                .filter(c -> c.getPointIndex() > curPathIdx)
                .min(Comparator.comparing(CheckPoint::getPointIndex));
    }

    private double calculateDuration(Point currentPoint, CheckPoint nextCheckPoint) {
        List<TableQueryResultDto> queryResultDtos = osrmTableService.getTableOfMultiDestDistanceAndDuration(
                coordinateToString(currentPoint),
                List.of(coordinateToString(nextCheckPoint.getCoordinate())));
        TableQueryResultDto tableQueryResultDto = queryResultDtos.get(0);
        return tableQueryResultDto.getDuration();
    }

    private boolean filterPathInCheckPoint(Long curPathIdx, CheckPoint nextCheckPoint, PathPoint targetPathPoint) {
        if (curPathIdx <= targetPathPoint.getPointIndex()
                && targetPathPoint.getPointIndex() <= nextCheckPoint.getPointIndex()) {
            return true;
        }
        MapLocation checkPointLocation
                = new MapLocation(nextCheckPoint.getCoordinate().getY(), nextCheckPoint.getCoordinate().getX());
        MapLocation pathPointLocation
                = new MapLocation(targetPathPoint.getCoordinate().getY(), targetPathPoint.getCoordinate().getX());
        double distance = CoordinateUtil.calculateDistance(checkPointLocation, pathPointLocation);

        return distance <= checkPointRadius;
    }

    private String coordinateToString(Point point) {
        return point.getX() + "," + point.getY();
    }

    private NavigationPath findNavigationPathByIdFetchJoin(Long naviPathId) {
        return navigationPathRepository.findNavigationPathByNaviPathIdFetchCheckPoints(naviPathId)
                .orElseThrow(() -> {
                    log.info("존재하지 않는 네비게이션 경로");
                    return new IllegalArgumentException("No Such Navigation Path");
                });
    }
}
