package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NavigationPathService {
    private final NavigationPathRepository navigationPathRepository;
    private final VehicleRepository vehicleRepository;

    public Optional<NavigationPath> findNavigationPathByVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> {
                    log.info("해당 id값의 vehicle이 존재하지 않음.");
                    return new IllegalArgumentException("해당 id값의 vehicle이 존재하지 않습니다.");
                });
        return navigationPathRepository.findNavigationPathByVehicle(vehicle);
    }


}
