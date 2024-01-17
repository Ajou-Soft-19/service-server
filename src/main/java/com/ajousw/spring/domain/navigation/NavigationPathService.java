package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NavigationPathService {
    private final NavigationPathRepository navigationPathRepository;
    private final VehicleRepository vehicleRepository;

    public Optional<NavigationPath> findNavigationPathByVehicle(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId).get();
        return navigationPathRepository.findNavigationPathByVehicle(vehicle);
    }


}
