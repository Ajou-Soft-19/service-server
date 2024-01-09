package com.ajousw.spring.domain.navigation.api.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class NaverNavigationPathInfo extends NavigationPathInfo {

    @SuppressWarnings("unchecked")
    public NaverNavigationPathInfo(Map<String, Object> attributes) {
        super(attributes);

        this.code = (int) attributes.get("code");
        this.message = (String) attributes.get("message");
        this.currentDateTime = (String) attributes.get("currentDateTime");

        Map<String, Object> route = (Map<String, Object>) attributes.get("route");

        List<Map<String, Object>> trafast = (List<Map<String, Object>>) route.get("trafast");

        for (Map<String, Object> queryResult : trafast) {
            Map<String, Object> summary = (Map<String, Object>) queryResult.get("summary");

            this.start = (List<Double>) ((Map<String, Object>) summary.get("start")).get("location");
            this.goal = (List<Double>) ((Map<String, Object>) summary.get("goal")).get("location");
            this.distance = (int) summary.get("distance");
            this.duration = (int) summary.get("duration");
            this.path = (List<Coordinate>) queryResult.get("path");

            this.guide = new ArrayList<>();
            List<Map<String, Object>> guideList = (List<Map<String, Object>>) queryResult.get("guide");
            for (Map<String, Object> guideItem : guideList) {
                this.guide.add(new Guide(guideItem));
            }
        }
    }
}

