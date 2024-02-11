package com.ajousw.spring.domain.navigation.api.provider;

import com.ajousw.spring.domain.navigation.api.info.route.NavigationApiResponse;
import com.ajousw.spring.domain.navigation.api.provider.factory.Provider;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OsrmNavigationApiTest {

    @Autowired
    private NavigationProvider navigationProvider;

    @Test
    void api_test() {
        Map<String, String> params = Map.of("start", "127.105336,37.352248", "goal",
                "127.11432998507473,37.378793096353206", "getSteps", "false");
        NavigationApiResponse navigationQueryResult = navigationProvider.getNavigationQueryResult(Provider.OSRM,
                params);

        log.info("{}", navigationQueryResult);
    }
}