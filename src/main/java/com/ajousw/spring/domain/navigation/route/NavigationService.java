package com.ajousw.spring.domain.navigation.route;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.navigation.api.NavigationPathProvider;
import com.ajousw.spring.domain.navigation.api.Provider;
import com.ajousw.spring.domain.navigation.api.info.route.Coordinate;
import com.ajousw.spring.domain.navigation.api.info.route.Guide;
import com.ajousw.spring.domain.navigation.api.info.route.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.navigation.dto.PathGuideDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.entity.BatchInsertJdbcRepository;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.NavigationPathRepository;
import com.ajousw.spring.domain.navigation.entity.PathGuide;
import com.ajousw.spring.domain.navigation.entity.PathGuideRepository;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
import com.ajousw.spring.domain.navigation.entity.PathPointRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NavigationService {
    private final NavigationPathProvider pathProvider;
    private final NavigationPathRepository navigationPathRepository;
    private final PathPointRepository pathPointRepository;
    private final BatchInsertJdbcRepository batchInsertJdbcRepository;
    private final PathGuideRepository pathGuideRepository;
    private final MemberJpaRepository memberRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    // TODO: API 라우트 별 정리
    public NavigationPathDto createNavigationPath(String email, Provider provider, Map<String, String> params,
                                                  String queryType, boolean saveResult) {
        Member member = findMemberByEmail(email);

        NavigationApiResponse navigationQueryResult = pathProvider.getNavigationQueryResult(provider, params);

        NavigationPath naviPath = createNaviPath(member, navigationQueryResult, provider, queryType,
                navigationQueryResult.getPaths().size());
        if (saveResult) {
            navigationPathRepository.save(naviPath);
        }

        List<PathGuide> pathGuides = createPathGuide(naviPath, navigationQueryResult.getGuides());
        List<PathPoint> pathPoints = createPathPoint(naviPath, navigationQueryResult.getPaths());
        if (saveResult) {
//            pathGuideRepository.saveAll(pathGuides);
            batchInsertJdbcRepository.saveAllInBatch(pathPoints);
        }

        return createNavigationPathDto(naviPath, pathPoints, pathGuides);
    }

    // TODO: 일반 차량은 guide 삭제
    @Transactional(readOnly = true)
    public NavigationPathDto getNavigationPathById(String email, Long naviPathId) {
        Member member = findMemberByEmail(email);
        NavigationPath navigationPath = findNavigationPathByIdFetchJoin(naviPathId);
        checkPathOwner(member, navigationPath);

        return createNavigationPathDto(navigationPath, navigationPath.getPathPoints(), navigationPath.getGuides());
    }

    public void updateCurrentPathPoint(String email, Long naviPathId, Long curPathIdx) {
        Member member = findMemberByEmail(email);
        NavigationPath navigationPath = findNavigationPathById(naviPathId);
        checkPathOwner(member, navigationPath);

        navigationPath.updateCurrentPathPoint(curPathIdx);
    }

    public void removeNavigationPath(String email, Long naviPathId) {
        Member member = findMemberByEmail(email);
        NavigationPath navigationPath = findNavigationPathById(naviPathId);
        checkPathOwner(member, navigationPath);

        pathGuideRepository.deleteAllInBatch(navigationPath.getGuides());
        pathPointRepository.deleteAllInBatch(navigationPath.getPathPoints());
        navigationPathRepository.deleteById(navigationPath.getNaviPathId());
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
        return navigationPathRepository.findNavigationPathByNaviPathIdFetchGuides(naviPathId)
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

    private NavigationPath createNaviPath(Member member, NavigationApiResponse navResponse, Provider provider,
                                          String queryType, int pathSize) {
        return NavigationPath.builder()
                .member(member)
                .vehicle(null) // TODO: vehicle 조회
                .isEmergencyPath(false)
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

    private List<PathGuide> createPathGuide(NavigationPath naviPath, List<Guide> guides) {
        List<PathGuide> pathGuides = new ArrayList<>();
        for (Guide guide : guides) {
            pathGuides.add(
                    new PathGuide(naviPath, guide.getPointIndex(), (long) guide.getType(), guide.getInstructions(),
                            guide.getDistance(), guide.getDuration()));
        }

        return pathGuides;
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
                                                      List<PathGuide> pathGuides) {
        List<PathPointDto> pathPointDtos = pathPoints.stream().map(PathPointDto::new).toList();
        List<PathGuideDto> pathGuideDtos = pathGuides.stream().map(PathGuideDto::new).toList();
        return new NavigationPathDto(navigationPath, pathPointDtos, pathGuideDtos);
    }
}
