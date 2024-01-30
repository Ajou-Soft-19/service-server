package com.ajousw.spring.domain.navigation.route;

import com.ajousw.spring.domain.navigation.EmergencyNavigationService;
import com.ajousw.spring.domain.navigation.NavigationService;
import com.ajousw.spring.domain.navigation.api.provider.Provider;
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
public class NaverNavigationService {

    private final NavigationService navigationService;
    private final EmergencyNavigationService emergencyNavigationService;

    public NavigationPathDto getNaverNavigationPath(String email, Long vehicleId, String source, String dest,
                                                    String option, boolean isEmergency) {
        Map<String, String> params = createParams(source, dest, Map.of("option", option));

        if (isEmergency) {
            return emergencyNavigationService.createNavigationPath(email, vehicleId, Provider.NAVER, params,
                    "Driving 5");
        }

        return navigationService.getNavigationPath(Provider.NAVER, params, "Driving 5");
    }

    private Map<String, String> createParams(String source, String dest, Map<String, String> options) {
        Map<String, String> params = new HashMap<>(options);
        params.put("start", source);
        params.put("goal", dest);
        return params;
    }
}
