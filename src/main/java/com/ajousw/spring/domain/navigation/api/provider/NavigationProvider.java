package com.ajousw.spring.domain.navigation.api.provider;

import com.ajousw.spring.domain.navigation.api.info.route.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.info.table.TableApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.factory.NavigationApi;
import com.ajousw.spring.domain.navigation.api.provider.factory.NavigationApiFactory;
import com.ajousw.spring.domain.navigation.api.provider.factory.Provider;
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
public class NavigationProvider {

    private ObjectMapper mapper;
    private final NavigationApiFactory navigationApiFactory;

    @PostConstruct
    public void init() {
        mapper = new ObjectMapper();
    }

    public NavigationApiResponse getNavigationQueryResult(Provider provider, Map<String, String> params) {
        NavigationApi navigationApi = navigationApiFactory.getNavigationApi(provider);
        ResponseEntity<String> response = navigationApi.getNavigationPathInfo(params);

        try {
            Map<String, Object> attributes = mapper.readValue(response.getBody(), Map.class);
            return navigationApiFactory.parseNavigationApiResponse(provider, attributes);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new RuntimeException("길찾기 API 파싱 중 오류 발생", e);
        }
    }

    public TableApiResponse getTableQueryResult(Provider provider, Map<String, Object> params) {
        NavigationApi navigationApi = navigationApiFactory.getNavigationApi(provider);
        ResponseEntity<String> response = navigationApi.getDistanceDurationTableInfo(params);

        try {
            Map<String, Object> attributes = mapper.readValue(response.getBody(), Map.class);
            return navigationApiFactory.parseTableApiResponse(provider, attributes);
        } catch (JsonProcessingException | NullPointerException e) {
            throw new RuntimeException("거리,시간 계산 API 파싱 중 오류 발생", e);
        }
    }

}
