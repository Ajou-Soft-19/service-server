package com.ajousw.spring.domain.navigation.api.info.route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NaverNavigationApiResponse extends NavigationApiResponse {

    @SuppressWarnings("unchecked")
    public NaverNavigationApiResponse(Map<String, Object> attributes) {
        super(attributes);

        this.code = (int) attributes.get("code");
        this.message = (String) attributes.get("message");
        this.currentDateTime = (String) attributes.get("currentDateTime");

        Map<String, Object> route = (Map<String, Object>) attributes.get("route");
        Map.Entry<String, Object> firstEntry = route.entrySet().iterator().next();
        List<Map<String, Object>> results = (List<Map<String, Object>>) firstEntry.getValue();

        for (Map<String, Object> queryResult : results) {
            Map<String, Object> summary = (Map<String, Object>) queryResult.get("summary");

            this.start = (List<Double>) ((Map<String, Object>) summary.get("start")).get("location");
            this.goal = (List<Double>) ((Map<String, Object>) summary.get("goal")).get("location");
            this.distance = ((Integer) summary.get("distance")).longValue();
            this.duration = ((Integer) summary.get("duration")).longValue();
            List<List<Double>> rawCoordinates = (List<List<Double>>) queryResult.get("path");
            this.paths = rawCoordinates.stream()
                    .map(Coordinate::new)
                    .toList();

            this.guides = new ArrayList<>();
            List<Map<String, Object>> guideList = (List<Map<String, Object>>) queryResult.get("guide");
            for (Map<String, Object> guideItem : guideList) {
                this.guides.add(new Guide(guideItem));
            }
        }
    }
}

