package com.ajousw.spring.domain.navigation.api;

import com.ajousw.spring.domain.navigation.api.factory.NaverApiFactory;
import com.ajousw.spring.domain.navigation.api.factory.Provider;
import com.ajousw.spring.domain.navigation.api.info.NaverNavigationPathInfo;
import com.ajousw.spring.domain.navigation.api.info.NavigationPathInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NavigationPathProvider {

    private ObjectMapper mapper;
    private final NaverApiFactory naverApiFactory;

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper();
    }

    public NavigationPathInfo getNavigationQueryResult(Provider provider, Map<String, String> params) {
        ResponseEntity<String> response = null;
        switch (provider) {
            case NAVER -> response = naverApiFactory.getNavigationPathInfo(params.get("start"), params.get("goal"),
                    params.get("option"));
            default -> throw new IllegalStateException("구현되지 않은 API");
        }

        try {
            Map<String, Object> attributes = mapper.readValue(response.getBody(), Map.class);
            return new NaverNavigationPathInfo(attributes);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new RuntimeException("길찾기 API 파싱 중 오류 발생", e);
        }
    }

}
