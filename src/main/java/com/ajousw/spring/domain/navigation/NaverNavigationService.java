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
public class NaverNavigationService {

    private final NavigationService navigationService;

    public NavigationPathDto getNaverNavigationPath(String email, String source, String dest, String option,
                                                    boolean saveResult) {
        Map<String, String> params = createParams(source, dest, Map.of("option", option));
        return navigationService.createNavigationPath(email, Provider.NAVER, params, "Driving 5", saveResult);
    }

    private Map<String, String> createParams(String source, String dest, Map<String, String> options) {
        Map<String, String> params = new HashMap<>(options);
        params.put("start", source);
        params.put("goal", dest);
        return params;
    }
}
