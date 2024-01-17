package com.ajousw.spring.domain.navigation.route;

import com.ajousw.spring.domain.navigation.EmergencyService;
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
    private final EmergencyService emergencyService;

    public NavigationPathDto getOsrmNavigationPath(String email, Long vehicleId, String source, String dest, String getSteps,
                                                   boolean saveResult, boolean isEmergency) {
        Map<String, String> params = createDrivingParams(source, dest, Map.of("getSteps", getSteps));

        if (isEmergency) {
            return emergencyService.createNavigationPath(email, vehicleId, Provider.OSRM, params, "OSRM");
        }
        
        return navigationService.createNavigationPath(email, vehicleId, Provider.OSRM, params, "OSRM", saveResult);
    }

    private Map<String, String> createDrivingParams(String source, String dest, Map<String, String> options) {
        Map<String, String> params = new HashMap<>(options);
        params.put("start", source);
        params.put("goal", dest);
        return params;
    }
}
