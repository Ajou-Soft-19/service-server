package com.ajousw.spring.domain.navigation.api.provider;

import com.ajousw.spring.domain.exception.ApiNotSupportedException;
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
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Getter
@Component
public class NaverNavigationApi implements NavigationApi {

    @Value("${navigation.api.naver.url}")
    private String requestUrl;

    @Value("${navigation.api.naver.client-id}")
    private String clientId;

    @Value("${navigation.api.naver.api-key}")
    private String apiKey;

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
                            params.get("option")))
                    .header("X-NCP-APIGW-API-KEY-ID", clientId)
                    .header("X-NCP-APIGW-API-KEY", apiKey)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();

            if (statusCode.isError()) {
                log.error("Naver Direction 5 api {} error", e.getStatusCode(), e);
                throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
            }
        } catch (WebClientRequestException e) {
            log.error("OSRM route api server down", e);
            throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
        }
        return response;
    }

    @Override
    public ResponseEntity<String> getDistanceDurationTableInfo(Map<String, Object> params) {
        throw new ApiNotSupportedException("지원하지 않는 API 입니다.");
    }

    private String setParams(String requestUrl, String start, String goal, String option) {
        return String.format(requestUrl, start, goal, option);
    }
}
