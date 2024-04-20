package com.ajousw.spring.domain.navigation.api;

import com.ajousw.spring.domain.navigation.api.exception.BadApiResponseException;
import com.ajousw.spring.domain.navigation.api.info.rev_geo.RevgeoDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;


@Slf4j
@Component
public class NaverReverseGeocodingService {

    @Value("${navigation.api.naver.rev-geo-url}")
    private String requestUrl;

    @Value("${navigation.api.naver.client-id}")
    private String clientId;

    @Value("${navigation.api.naver.api-key}")
    private String apiKey;

    private WebClient webClient;

    private ObjectMapper mapper;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder().build();
        mapper = new ObjectMapper();
    }

    public RevgeoDto getReverseGeocodingInfo(double latitude, double longitude) {
        ResponseEntity<String> response = getReverseGeocodingResponse(latitude + "," + longitude);
        try {
            Map<String, Object> attributes = mapper.readValue(response.getBody(), Map.class);
            return new RevgeoDto(attributes);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new RuntimeException("길찾기 API 파싱 중 오류 발생", e);
        }
    }

    private ResponseEntity<String> getReverseGeocodingResponse(String coords) {
        ResponseEntity<String> response = null;
        try {
            response = webClient.get()
                    .uri(getRequestUrl(coords))
                    .header("X-NCP-APIGW-API-KEY-ID", clientId)
                    .header("X-NCP-APIGW-API-KEY", apiKey)
                    .retrieve()
                    .toEntity(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            HttpStatusCode statusCode = e.getStatusCode();
            if (statusCode.is4xxClientError()) {
                throw new BadApiResponseException("잘못된 요청입니다.");
            }
            if (statusCode.isError()) {
                log.error("Naver Reverse Geocoding API error", e.getStatusCode(), e);
                throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
            }
        } catch (WebClientRequestException e) {
            log.error("Naver Reverse Geocoding API server down", e);
            throw new BadApiResponseException("API 서버에 오류가 발생했습니다.");
        }
        return response;
    }

    private String getRequestUrl(String coords) {
        return requestUrl + "?coords=" + coords + "&output=json";
    }

}
