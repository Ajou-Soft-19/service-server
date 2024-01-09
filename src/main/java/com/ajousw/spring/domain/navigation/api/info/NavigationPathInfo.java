package com.ajousw.spring.domain.navigation.api.info;

import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.ToString;

@Getter
public abstract class NavigationPathInfo {
    protected Map<String, Object> attributes;

    protected int code;
    protected String message;
    protected String currentDateTime;

    protected List<Double> start;
    protected List<Double> goal;
    protected int distance;
    protected int duration;

    protected List<Coordinate> path;
    protected List<Guide> guide;

    public NavigationPathInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Getter
    @ToString
    public static class Guide {
        private final int pointIndex;
        private final int type;
        private final String instructions;
        private final int distance;
        private final int duration;

        public Guide(Map<String, Object> guideInfo) {
            this.pointIndex = (int) guideInfo.get("pointIndex");
            this.type = (int) guideInfo.get("type");
            this.instructions = (String) guideInfo.get("instructions");
            this.distance = (int) guideInfo.get("distance");
            this.duration = (int) guideInfo.get("duration");
        }
    }

    @Getter
    @ToString
    public static class Coordinate {
        private final double longitude;
        private final double latitude;

        public Coordinate(List<Double> pathInfo) {
            this.longitude = (double) pathInfo.get(0);
            this.latitude = (double) pathInfo.get(1);
        }
    }

    @Override
    public String toString() {
        return "NavigationPathInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", currentDateTime='" + currentDateTime + '\'' +
                ", start=" + start +
                ", goal=" + goal +
                ", distance=" + distance +
                ", duration=" + duration +
                ", path=" + path +
                ", guide=" + guide +
                '}';
    }
}
