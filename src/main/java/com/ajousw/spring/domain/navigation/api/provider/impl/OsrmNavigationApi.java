package com.ajousw.spring.domain.navigation.api.provider.impl;

import com.ajousw.spring.domain.navigation.api.exception.BadApiResponseException;
import com.ajousw.spring.domain.navigation.api.provider.factory.NavigationApi;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Getter
@Component
public class OsrmNavigationApi implements NavigationApi {

    @Value("${navigation.api.osrm.driving-url}")
    private String drivingRequestUrl;

    @Value("${navigation.api.osrm.table-url}")
    private String tableRequestUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder().build();
    }

    public ResponseEntity<String> getNavigationPathInfo(Map<String, String> params) {
        ResponseEntity<String> response = null;
        try {
            response = webClient.get()
                    .uri(setDrivingParams(drivingRequestUrl, params.get("start"), params.get("goal"),
                            "getSteps".equals(params.get("getSteps"))))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.is4xxClientError()) {
                throw new BadApiResponseException("잘못된 요청입니다.");
            }
            if (statusCode.isError()) {
                log.error("OSRM route api {} error", e.getStatusCode(), e);
                throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
            }
        } catch (WebClientRequestException e) {
            log.error("OSRM route api server down", e);
            throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
        }
        return response;
    }

    public ResponseEntity<String> getDistanceDurationTableInfo(Map<String, Object> params) {
        ResponseEntity<String> response = null;
        try {
            response = webClient.get()
                    .uri(setTableParams(tableRequestUrl, (List<String>) params.get("sources"),
                            (List<String>) params.get("destinations"), (List<Double>) params.get("directions")))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.isError()) {
                log.error("OSRM table api {} error", e.getStatusCode(), e);
                throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
            }
        }

        return response;
    }

    private String setDrivingParams(String requestUrl, String start, String goal, boolean getSteps) {
        return String.format(requestUrl, start, goal, getSteps);
    }

    private String setTableParams(String requestUrl, List<String> sources, List<String> destinations,
                                  List<Double> directions) {
        List<String> coordinates = new ArrayList<>();
        coordinates.addAll(sources);
        coordinates.addAll(destinations);
        String coordinatesString = String.join(";", coordinates);

        String startPointString = IntStream.rangeClosed(0, sources.size() - 1)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(";"));

        String destinationString = IntStream.rangeClosed(sources.size(), sources.size() + destinations.size() - 1)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(";"));

        String formattedRequestUrl = String.format(requestUrl, coordinatesString, startPointString, destinationString);
        if (directions == null) {
            return formattedRequestUrl;
        }

        if (sources.size() != directions.size()) {
            throw new IllegalArgumentException("Source size and Direction size are not equal");
        }

        String bearingsString = directions.stream()
                .map(direction -> direction + ",20")
                .collect(Collectors.joining(";"));

        return String.format("%s&bearings=%s", formattedRequestUrl, bearingsString);
    }

}
