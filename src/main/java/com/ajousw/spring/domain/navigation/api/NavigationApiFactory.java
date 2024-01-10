package com.ajousw.spring.domain.navigation.api;


import com.ajousw.spring.domain.navigation.api.info.NaverNavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.info.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.info.OsrmNavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.NaverNavigationApi;
import com.ajousw.spring.domain.navigation.api.provider.NavigationApi;
import com.ajousw.spring.domain.navigation.api.provider.OsrmNavigationApi;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationApiFactory {
    private final NaverNavigationApi naverNavigationApi;
    private final OsrmNavigationApi osrmNavigationApi;

    public NavigationApi getNavigationApi(Provider provider) {
        return switch (provider) {
            case NAVER -> naverNavigationApi;
            case OSRM -> osrmNavigationApi;
            default -> throw new IllegalArgumentException("Invalid provider type");
        };
    }

    public NavigationApiResponse parseApiResponse(Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case NAVER -> new NaverNavigationApiResponse(attributes);
            case OSRM -> new OsrmNavigationApiResponse(attributes);
            default -> throw new IllegalArgumentException("Invalid provider type");
        };
    }
}
