package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.api.NavigationPathProvider;
import com.ajousw.spring.domain.navigation.api.Provider;
import com.ajousw.spring.domain.navigation.api.info.Coordinate;
import com.ajousw.spring.domain.navigation.api.info.Guide;
import com.ajousw.spring.domain.navigation.api.info.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.navigation.dto.PathGuideDto;
import com.ajousw.spring.domain.navigation.dto.PathPointDto;
import com.ajousw.spring.domain.navigation.route.entity.MapLocation;
import com.ajousw.spring.domain.navigation.route.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.route.entity.NavigationPathRepository;
import com.ajousw.spring.domain.navigation.route.entity.PathGuide;
import com.ajousw.spring.domain.navigation.route.entity.PathGuideRepository;
import com.ajousw.spring.domain.navigation.route.entity.PathPoint;
import com.ajousw.spring.domain.navigation.route.entity.PathPointRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final PathGuideRepository pathGuideRepository;

    // TODO: ApiParams 클래스 추가, 코드 정리
    public NavigationPathDto getNavigationPath(Provider provider, Map<String, String> params, String queryType,
                                               boolean saveResult) {
        NavigationApiResponse navigationQueryResult = pathProvider.getNavigationQueryResult(provider, params);

        NavigationPath naviPath = createNaviPath(navigationQueryResult, provider, queryType);
        if (saveResult) {
            navigationPathRepository.save(naviPath);
        }

        List<PathGuide> pathGuides = createPathGuide(naviPath, navigationQueryResult.getGuides());
        List<PathPoint> pathPoints = createPathPoint(naviPath, navigationQueryResult.getPaths());
        if (saveResult) {
            pathGuideRepository.saveAll(pathGuides);
            pathPointRepository.saveAll(pathPoints);
        }

        return createNavigationPathDto(naviPath, pathPoints, pathGuides);
    }

    private NavigationPathDto createNavigationPathDto(NavigationPath navigationPath, List<PathPoint> pathPoints,
                                                      List<PathGuide> pathGuides) {
        List<PathPointDto> pathPointDtos = pathPoints.stream().map(PathPointDto::new).toList();
        List<PathGuideDto> pathGuideDtos = pathGuides.stream().map(PathGuideDto::new).toList();
        return new NavigationPathDto(navigationPath, pathPointDtos, pathGuideDtos);
    }

    private NavigationPath createNaviPath(NavigationApiResponse navResponse, Provider provider, String queryType) {
        return NavigationPath.builder()
                .provider(provider)
                .sourceLocation(new MapLocation(navResponse.getStart().get(0), navResponse.getStart().get(1)))
                .destLocation(new MapLocation(navResponse.getGoal().get(0), navResponse.getGoal().get(1)))
                .queryType(queryType)
                .distance(navResponse.getDistance())
                .duration(navResponse.getDuration())
                .currentPathPoint(0L)
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
                    path.getLatitude(), path.getLongitude()));
            index++;
        }

        return pathPoints;
    }
}
