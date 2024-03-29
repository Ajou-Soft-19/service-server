package com.ajousw.spring.domain.navigation.api.provider.factory;

import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface NavigationApi {
    ResponseEntity<String> getNavigationPathInfo(Map<String, String> params);

    ResponseEntity<String> getDistanceDurationTableInfo(Map<String, Object> params);
}
