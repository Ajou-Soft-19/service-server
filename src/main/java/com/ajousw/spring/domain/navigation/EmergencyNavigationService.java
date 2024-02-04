package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.navigation.api.info.route.Coordinate;
import com.ajousw.spring.domain.navigation.api.info.route.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.NavigationPathProvider;
import com.ajousw.spring.domain.navigation.api.provider.Provider;
import com.ajousw.spring.domain.navigation.dto.CheckPointDto;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.dto.TableQueryResultDto;
import com.ajousw.spring.domain.navigation.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.navigation.entity.repository.BatchInsertJdbcRepository;
import com.ajousw.spring.domain.navigation.entity.repository.CheckPointRepository;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.navigation.entity.repository.PathPointRepository;
import com.ajousw.spring.domain.navigation.route.OsrmTableService;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;
import com.ajousw.spring.domain.warn.AlertService;
import com.ajousw.spring.domain.warn.util.CoordinateUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class EmergencyNavigationService {
    private final NavigationPathProvider pathProvider;
    private final VehicleRepository vehicleRepository;
    private final MemberJpaRepository memberRepository;
    private final PathPointRepository pathPointRepository;
    private final CheckPointRepository checkPointRepository;
    private final NavigationPathRepository navigationPathRepository;
    private final BatchInsertJdbcRepository batchInsertJdbcRepository;
    private final AlertService alertService;
    private final OsrmTableService osrmTableService;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Value("${emergency.check-point-distance}")
    private double checkPointDistance;

    @Value("${emergency.check-point-radius}")
    private double checkPointRadius;

    @Value("${emergency.filter-radius}")
    private Double filterRadius;

    public NavigationPathDto createNavigationPath(String email, Long vehicleId, Provider provider,
                                                  Map<String, String> params,
                                                  String queryType) {
        Member member = findMemberByEmail(email);
        Vehicle vehicle = findVehicleById(vehicleId);
        checkVehicleOwner(member, vehicle);
//        deleteOldNavigationPath(vehicle);

        NavigationApiResponse navigationQueryResult = pathProvider.getNavigationQueryResult(provider, params);

        NavigationPath naviPath = createNaviPath(member, vehicle, navigationQueryResult, provider, queryType,
                navigationQueryResult.getPaths().size());
        navigationPathRepository.save(naviPath);

        List<PathPoint> pathPoints = createPathPoint(naviPath, navigationQueryResult.getPaths());
        List<CheckPoint> checkPoints = calculateCheckPoints(naviPath, pathPoints);
        batchInsertJdbcRepository.saveAllPathPointsInBatch(pathPoints);
        batchInsertJdbcRepository.saveAllCheckPointsInBatch(checkPoints);

        return createNavigationPathDto(naviPath, pathPoints, checkPoints);
    }

    // 삭제 예정
    private void deleteOldNavigationPath(Vehicle vehicle) {
        Optional<NavigationPath> oldNaviPathOptional = navigationPathRepository.
                findNavigationPathByVehicle(vehicle);

        if (oldNaviPathOptional.isEmpty()) {
            return;
        }

        NavigationPath navigationPath = oldNaviPathOptional.get();
        checkPointRepository.deleteAllByNavigationPathId(navigationPath.getNaviPathId());
        pathPointRepository.deleteByNavigationPathId(navigationPath.getNaviPathId());
        navigationPathRepository.deleteById(navigationPath.getNaviPathId());
        navigationPathRepository.flush();
    }

    @Transactional(readOnly = true)
    public NavigationPathDto getNavigationPathById(String email, Long naviPathId) {
        Member member = findMemberByEmail(email);
        NavigationPath navigationPath = findNavigationPathByIdFetchJoin(naviPathId);
        checkPathOwner(member, navigationPath);

        return createNavigationPathDto(navigationPath, navigationPath.getPathPoints(),
                navigationPath.getCheckPoints());
    }

    // 어드민 조회용
    @Transactional(readOnly = true)
    public NavigationPathDto getNavigationPathById(Long naviPathId) {
        NavigationPath navigationPath = findNavigationPathByIdFetchJoin(naviPathId);
        return createNavigationPathDto(navigationPath, navigationPath.getPathPoints(),
                navigationPath.getCheckPoints());
    }

    public void removeNavigationPath(String email, Long naviPathId) {
        Member member = findMemberByEmail(email);
        NavigationPath navigationPath = findNavigationPathById(naviPathId);
        checkPathOwner(member, navigationPath);

        checkPointRepository.deleteAllByNavigationPathId(navigationPath.getNaviPathId());
        pathPointRepository.deleteByNavigationPathId(navigationPath.getNaviPathId());
        navigationPathRepository.deleteById(navigationPath.getNaviPathId());
        navigationPathRepository.flush();
    }

    public Optional<CheckPointDto> updateCurrentPathPoint(Long naviPathId, Long emergencyEventId, Long curPathIdx) {
        NavigationPath navigationPath = findNavigationPathByIdFetchJoin(naviPathId);

        Long oldPathIdx = navigationPath.getCurrentPathPoint();
        List<CheckPoint> checkPoints = navigationPath.getCheckPoints();
        navigationPath.updateCurrentPathPoint(curPathIdx);
        log.info("updated pathPoint for vehicleId {} naviPathId {} pathIndex {}",
                navigationPath.getVehicle().getVehicleId(), navigationPath.getNaviPathId(), curPathIdx);

        return alertNextCheckPointIfPassedCheckPoint(navigationPath, emergencyEventId, oldPathIdx, curPathIdx,
                checkPoints);
    }

    // TODO: 다음 체크포인트까지의 경로를 보장하기
    private Optional<CheckPointDto> alertNextCheckPointIfPassedCheckPoint(NavigationPath navigationPath,
                                                                          Long emergencyEventId,
                                                                          Long oldPathIdx,
                                                                          Long curPathIdx,
                                                                          List<CheckPoint> checkPoints) {
        Optional<CheckPoint> nextCheckPointOptional = findNextCheckPoint(curPathIdx, oldPathIdx,
                checkPoints);
        if (nextCheckPointOptional.isEmpty()) {
            return Optional.empty();
        }

        double duration = calculateDuration(curPathIdx, navigationPath, oldPathIdx);

        CheckPoint nextCheckPoint = nextCheckPointOptional.get();
        List<PathPointDto> filteredPathPoints = navigationPath.getPathPoints().stream()
                .filter(p -> filterPathInCheckPoint(curPathIdx, nextCheckPoint, p))
                .map(PathPointDto::new).toList();

        alertService.alertNextCheckPoint(navigationPath, emergencyEventId, filteredPathPoints, nextCheckPoint, duration,
                navigationPath.getVehicle().getLicenceNumber(), navigationPath.getVehicle().getVehicleType());

        return Optional.of(new CheckPointDto(nextCheckPoint, duration));
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

    private double calculateDuration(Long curPathIdx, NavigationPath navigationPath, Long oldPathIdx) {
        PathPoint prevPathPoint = navigationPath.getPathPoints().get(oldPathIdx.intValue());
        PathPoint curPathPoint = navigationPath.getPathPoints().get(curPathIdx.intValue());
        List<TableQueryResultDto> queryResultDtos = osrmTableService.getTableOfDistancesAndDurations(
                coordinateToString(prevPathPoint.getCoordinate()),
                List.of(coordinateToString(curPathPoint.getCoordinate())));
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

    private void checkVehicleOwner(Member member, Vehicle vehicle) {
        if (!vehicle.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Not Owner of vehicle");
        }
    }

    private void checkPathOwner(Member member, NavigationPath navigationPath) {
        if (!navigationPath.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Not User of Navigation Path");
        }
    }

    private NavigationPath findNavigationPathById(Long naviPathId) {
        return navigationPathRepository.findById(naviPathId).orElseThrow(() -> {
            log.info("존재하지 않는 네비게이션 경로");
            return new IllegalArgumentException("No Such Navigation Path");
        });
    }

    private NavigationPath findNavigationPathByIdFetchJoin(Long naviPathId) {
        return navigationPathRepository.findNavigationPathByNaviPathIdFetchCheckPoints(naviPathId)
                .orElseThrow(() -> {
                    log.info("존재하지 않는 네비게이션 경로");
                    return new IllegalArgumentException("No Such Navigation Path");
                });
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("계정이 존재하지 않음");
            return new IllegalArgumentException("No Such Member");
        });
    }

    private Vehicle findVehicleById(Long vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId).orElseThrow(() -> {
            log.info("차량 ID가 존재하지 않습니다.");
            return new IllegalArgumentException("No Such Vehicle");
        });
    }

    /**
     * 주어진 경로 포인트 리스트에서 wayPointDistance 간격으로 체크포인트를 계산합니다.
     * <p>
     * 이 메소드는 주어진 {@code PathPoint} 리스트를 순회하며 각 포인트 사이의 거리를 누적합니다. 누적 거리가 wayPointDistance 이상이 될 때마다 해당 포인트를 체크포인트로
     * 선정하고, 체크포인트 리스트에 추가합니다. 누적 거리는 체크포인트를 추가한 후 0으로 재설정됩니다. 첫 포인트와 마지막 포인트는 무시합니다.
     * </p>
     *
     * @param pathPoints {@code PathPoint} 객체의 리스트
     * @return {@code CheckPoints} 객체의 리스트, 각각은 wayPointDistance 간격의 체크포인트를 나타냅니다.
     * @see PathPoint
     * @see CheckPoint
     */
    private List<CheckPoint> calculateCheckPoints(NavigationPath navigationPath, List<PathPoint> pathPoints) {
        List<CheckPoint> checkPoints = new ArrayList<>();
        PathPoint previousPoint = pathPoints.get(0);
        double accumulatedDistance = 0;

        for (int i = 1; i < pathPoints.size() - 1; i++) {
            PathPoint currentPoint = pathPoints.get(i);
            double currentLat = currentPoint.getCoordinate().getY();
            double currentLon = currentPoint.getCoordinate().getX();

            double distance = CoordinateUtil.calculateDistance(
                    new MapLocation(previousPoint.getCoordinate().getY(),
                            previousPoint.getCoordinate().getX()),
                    new MapLocation(currentLat, currentLon)
            );

            accumulatedDistance += distance;
            if (accumulatedDistance < checkPointDistance) {
                continue;
            }
//            계산량이 너무 많아서 경로 이탈 재계산 고려 update 시마다 그때그때 계산?
//            List<TableQueryResultDto> queryResultDtos = tableService.getTableOfDistancesAndDurations(
//                    coordinateToString(previousPoint.getCoordinate()),
//                    List.of(coordinateToString(currentPoint.getCoordinate())));
//
//            TableQueryResultDto tableQueryResultDto = queryResultDtos.get(0);
//            Double duration = tableQueryResultDto.getDuration();

            checkPoints.add(
                    new CheckPoint(navigationPath, currentPoint.getCoordinate(),
                            (long) i, accumulatedDistance, 0.0));
            accumulatedDistance = 0;

            previousPoint = currentPoint;
        }

        return checkPoints;
    }

    private String coordinateToString(Point point) {
        return point.getX() + "," + point.getY();
    }

    private NavigationPath createNaviPath(Member member, Vehicle vehicle, NavigationApiResponse navResponse,
                                          Provider provider,
                                          String queryType, int pathSize) {
        return NavigationPath.builder()
                .member(member)
                .vehicle(vehicle)
                .isEmergencyPath(true)
                .provider(provider)
                .sourceLocation(new MapLocation(navResponse.getStart().get(0), navResponse.getStart().get(1)))
                .destLocation(new MapLocation(navResponse.getGoal().get(0), navResponse.getGoal().get(1)))
                .queryType(queryType)
                .distance(navResponse.getDistance())
                .duration(navResponse.getDuration())
                .currentPathPoint(0L)
                .pathPointSize((long) pathSize)
                .build();
    }


    private List<PathPoint> createPathPoint(NavigationPath naviPath, List<Coordinate> paths) {
        Long index = 0L;
        List<PathPoint> pathPoints = new ArrayList<>();
        for (Coordinate path : paths) {
            pathPoints.add(new PathPoint(naviPath, index,
                    geometryFactory.createPoint(
                            new org.locationtech.jts.geom.Coordinate(path.getLongitude(), path.getLatitude()))));
            index++;
        }

        return pathPoints;
    }

    private NavigationPathDto createNavigationPathDto(NavigationPath navigationPath, List<PathPoint> pathPoints,
                                                      List<CheckPoint> checkPoints) {
        List<PathPointDto> pathPointDtos = pathPoints.stream().map(PathPointDto::new).toList();
        List<CheckPointDto> checkPointDtos = checkPoints.stream().map(CheckPointDto::new).toList();
        return new NavigationPathDto(navigationPath, pathPointDtos, checkPointDtos,
                navigationPath.getIsEmergencyPath());
    }
}
