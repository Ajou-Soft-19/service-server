package com.ajousw.spring.domain.navigation.api.provider;

import java.util.Map;
import org.springframework.http.ResponseEntity;

public interface NavigationApi {
    ResponseEntity<String> getNavigationPathInfo(Map<String, String> params);
}
