package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.api.info.route.Coordinate;
import com.ajousw.spring.domain.navigation.api.info.route.Guide;
import com.ajousw.spring.domain.navigation.api.info.route.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.NavigationProvider;
import com.ajousw.spring.domain.navigation.api.provider.factory.Provider;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.navigation.dto.PathGuideDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PathGuide;
import com.ajousw.spring.domain.navigation.entity.PathPoint;
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
    private final NavigationProvider pathProvider;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public NavigationPathDto getNavigationPath(Provider provider, Map<String, String> params,
                                               String queryType) {
        NavigationApiResponse navigationQueryResult = pathProvider.getNavigationQueryResult(provider, params);

        NavigationPath naviPath = createNaviPath(navigationQueryResult, provider, queryType,
                navigationQueryResult.getPaths().size());

        List<PathGuide> pathGuides = createPathGuide(naviPath, navigationQueryResult.getGuides());
        List<PathPoint> pathPoints = createPathPoint(naviPath, navigationQueryResult.getPaths());

        return createNavigationPathDto(naviPath, pathPoints, pathGuides);
    }

    private NavigationPath createNaviPath(NavigationApiResponse navResponse,
                                          Provider provider,
                                          String queryType, int pathSize) {
        return NavigationPath.builder()
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
