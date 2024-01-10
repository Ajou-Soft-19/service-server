package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.api.Provider;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OsrmNavigationService {

    private final NavigationService navigationService;

    public NavigationPathDto getOsrmNavigationPath(String email, String source, String dest, String getSteps,
                                                   boolean saveResult) {
        Map<String, String> params = createParams(source, dest, Map.of("getSteps", getSteps));
        return navigationService.getNavigationPath(Provider.OSRM, params, "OSRM", saveResult);
    }

    private Map<String, String> createParams(String source, String dest, Map<String, String> options) {
        Map<String, String> params = new HashMap<>(options);
        params.put("start", source);
        params.put("goal", dest);
        return params;
    }
}
