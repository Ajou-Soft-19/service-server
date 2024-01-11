package com.ajousw.spring.domain.navigation.api.provider;

import com.ajousw.spring.domain.exception.BadApiResponseException;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Getter
@Component
public class OsrmNavigationApi implements NavigationApi {
    @Value("${navigation.api.osrm.url}")
    private String requestUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder().build();
    }

    public ResponseEntity<String> getNavigationPathInfo(Map<String, String> params) {
        ResponseEntity<String> response = null;
        try {
            response = webClient.get()
                    .uri(setParams(requestUrl, params.get("start"), params.get("goal"),
                            "getSteps".equals(params.get("getSteps"))))
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();

            if (statusCode.isError()) {
                log.error("OSRM route api {} error", e.getStatusCode(), e);
                throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
            }
        }
        return response;
    }

    private String setParams(String requestUrl, String start, String goal, boolean getSteps) {
        return String.format(requestUrl, start, goal, getSteps);
    }
}
