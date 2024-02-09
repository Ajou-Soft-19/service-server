package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.navigation.route.OsrmTableService;
import com.ajousw.spring.domain.warn.AlertService;
import com.ajousw.spring.domain.warn.util.CoordinateUtil;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
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

    @Value("${emergency.check-point-radius}")
    private double checkPointRadius;

    public void updateCurrentPathPoint(Long naviPathId, Long emergencyEventId, Long curPathIdx) {
        NavigationPath navigationPath = findNavigationPathByIdFetchJoin(naviPathId);

        Long oldPathIdx = navigationPath.getCurrentPathPoint();
        List<CheckPoint> checkPoints = navigationPath.getCheckPoints();
        navigationPath.updateCurrentPathPoint(curPathIdx);
        log.info("updated pathPoint for vehicleId {} naviPathId {} pathIndex {}",
                navigationPath.getVehicle().getVehicleId(), navigationPath.getNaviPathId(), curPathIdx);

        alertNextCheckPoint(navigationPath, emergencyEventId, oldPathIdx, curPathIdx,
                checkPoints);
    }

    // TODO: 다음 체크포인트까지의 경로를 보장하기
    private void alertNextCheckPoint(NavigationPath navigationPath,
                                     Long emergencyEventId,
                                     Long oldPathIdx,
                                     Long curPathIdx,
                                     List<CheckPoint> checkPoints) {
        Optional<CheckPoint> nextCheckPointOptional = findNextCheckPoint(curPathIdx, oldPathIdx,
                checkPoints);
        CheckPoint nextCheckPoint;
        // 체크포인트가 하나도 없는 경우를 처리하기 위한 예외
        nextCheckPoint = nextCheckPointOptional.orElseGet(() -> checkPoints.stream()
                .filter(c -> c.getPointIndex() <= curPathIdx)
                .max(Comparator.comparing(CheckPoint::getPointIndex))
                .orElse(null));

        if (nextCheckPoint == null) {
            return;
        }
        navigationPath.updateCheckPoint(nextCheckPoint.getPointIndex());

        List<PathPointDto> filteredPathPoints = navigationPath.getPathPoints().stream()
                .filter(p -> filterPathInCheckPoint(curPathIdx, nextCheckPoint, p))
                .map(PathPointDto::new).toList();

        double duration = calculateDuration(curPathIdx, nextCheckPoint, navigationPath);

        alertService.alertNextCheckPoint(navigationPath, emergencyEventId, filteredPathPoints, nextCheckPoint, duration,
                navigationPath.getVehicle().getLicenceNumber(), navigationPath.getVehicle().getVehicleType());
    }

    private Optional<CheckPoint> findNextCheckPoint(Long curPathIdx, Long oldPathIdx, List<CheckPoint> checkPoints) {
        Optional<CheckPoint> previousCheckPointOptional = checkPoints.stream()
                .filter(c -> c.getPointIndex() > oldPathIdx && c.getPointIndex() <= curPathIdx)
                .max(Comparator.comparing(CheckPoint::getPointIndex));

        if (previousCheckPointOptional.isEmpty()) {
            return Optional.empty();
        }

        CheckPoint previousCheckPoint = previousCheckPointOptional.get();

        return checkPoints.stream()
                .filter(c -> c.getPointIndex() > previousCheckPoint.getPointIndex())
                .min(Comparator.comparing(CheckPoint::getPointIndex));
    }

    private double calculateDuration(Long curPathIdx, CheckPoint nextCheckPoint, NavigationPath navigationPath) {
        PathPoint curPathPoint = navigationPath.getPathPoints().get(curPathIdx.intValue());
        List<TableQueryResultDto> queryResultDtos = osrmTableService.getTableOfMultiDestDistanceAndDuration(
                coordinateToString(curPathPoint.getCoordinate()),
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
