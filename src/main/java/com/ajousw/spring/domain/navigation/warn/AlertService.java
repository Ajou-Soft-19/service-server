package com.ajousw.spring.domain.navigation.warn;

import com.ajousw.spring.domain.navigation.route.entity.CheckPoint;
import com.ajousw.spring.domain.navigation.route.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.route.entity.PathPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    // TODO: Function X 구현
    public void alertNextCheckPoint(NavigationPath navigationPath, List<PathPoint> pathPoint,
                                    CheckPoint nextCheckPoint) {
        return;
    }
}
