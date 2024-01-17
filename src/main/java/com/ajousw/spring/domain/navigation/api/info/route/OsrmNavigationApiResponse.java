package com.ajousw.spring.domain.navigation.api.info.route;

import com.ajousw.spring.domain.navigation.api.info.SafeNumberParser;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class OsrmNavigationApiResponse extends NavigationApiResponse {

    @SuppressWarnings("unchecked")
    public OsrmNavigationApiResponse(Map<String, Object> attributes, SafeNumberParser safeNumberParser) {
        super(attributes);

        this.code = 200;
        this.message = (String) attributes.get("message");
        this.currentDateTime = (new Date()).toString();

        List<Map<String, Object>> routes = (List<Map<String, Object>>) attributes.get("routes");
        for (Map<String, Object> route : routes) {
            Map<String, Object> geometry = (Map<String, Object>) route.get("geometry");
            this.duration = safeNumberParser.convertToDoubleSafely(route.get("duration")).longValue();
            this.distance = safeNumberParser.convertToDoubleSafely(route.get("distance")).longValue();
            List<List<Double>> rawCoordinates = safeNumberParser.parseNestedListSafely(geometry.get("coordinates"));
            this.paths = rawCoordinates.stream()
                    .map(Coordinate::new)
                    .collect(Collectors.toList());

            this.guides = new ArrayList<>();
//            List<Map<String, Object>> guideList = (List<Map<String, Object>>) route.get("guide");
//            for (Map<String, Object> guideItem : guideList) {
//                this.guide.add(new Guide(guideItem));
//            }
        }

        List<Map<String, Object>> wayPoints = (List<Map<String, Object>>) attributes.get("waypoints");
        this.start = safeNumberParser.parseListSafely(wayPoints.get(0).get("location"));
        this.goal = safeNumberParser.parseListSafely(wayPoints.get(1).get("location"));
    }
}
