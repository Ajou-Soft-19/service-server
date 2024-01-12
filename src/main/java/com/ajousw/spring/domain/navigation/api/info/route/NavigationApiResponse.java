package com.ajousw.spring.domain.navigation.api.info.route;

import java.util.List;
import java.util.Map;
import lombok.Data;

/**
 * TODO: 경로가 한 개만 반환됨 -> 나중에 다중 경로로 리펙토링
 */
@Data
public class NavigationApiResponse {
    protected Map<String, Object> attributes;

    protected int code;
    protected String message;
    protected String currentDateTime;

    protected List<Double> start;
    protected List<Double> goal;
    protected Long distance;
    protected Long duration;

    protected List<Coordinate> paths;
    protected List<Guide> guides;

    public NavigationApiResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
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
                ", path=" + paths +
                ", guide=" + guides +
                '}';
    }
}
