package com.ajousw.spring.domain.navigation.api.provider.factory;


import com.ajousw.spring.domain.navigation.api.exception.ApiNotSupportedException;
import com.ajousw.spring.domain.navigation.api.info.SafeNumberParser;
import com.ajousw.spring.domain.navigation.api.info.route.NaverNavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.info.route.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.info.route.OsrmNavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.info.table.TableApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.impl.NaverNavigationApi;
import com.ajousw.spring.domain.navigation.api.provider.impl.OsrmNavigationApi;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationApiFactory {
    private final NaverNavigationApi naverNavigationApi;
    private final OsrmNavigationApi osrmNavigationApi;
    private final SafeNumberParser safeNumberParser;

    public NavigationApi getNavigationApi(Provider provider) {
        return switch (provider) {
            case NAVER -> naverNavigationApi;
            case OSRM -> osrmNavigationApi;
            default -> throw new IllegalArgumentException("Invalid provider type");
        };
    }

    public NavigationApiResponse parseNavigationApiResponse(Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case NAVER -> new NaverNavigationApiResponse(attributes);
            case OSRM -> new OsrmNavigationApiResponse(attributes, safeNumberParser);
            default -> throw new IllegalArgumentException("Invalid provider type");
        };
    }

    public TableApiResponse parseTableApiResponse(Provider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case NAVER -> throw new ApiNotSupportedException("Invalid provider type");
            case OSRM -> new TableApiResponse(attributes, safeNumberParser);
            default -> throw new IllegalArgumentException("Invalid provider type");
        };
    }
}
