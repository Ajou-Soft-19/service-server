package com.ajousw.spring.domain.vehicle.entity.repository;

import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class VehicleStatusRepositoryTest {

    @Autowired
    private VehicleStatusRepository vehicleStatusRepository;

    @Test
    void findAllWithinRadius() {
        List<VehicleStatus> allWithinRadius =
                vehicleStatusRepository.findAllWithinRadius(126.027956, 36.497979, 80000);

        List<VehicleStatus> allWithinRadius2 =
                vehicleStatusRepository.findAllWithinRadius(126.027956, 36.497979, 95000);
        log.info("{} {}", allWithinRadius.size(), allWithinRadius);
        log.info("{} {}", allWithinRadius2.size(), allWithinRadius2);
    }
}