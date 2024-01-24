package com.ajousw.spring.domain.vehicle.entity.repository;

import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleId(Long vehicleId);

    boolean existsByLicenceNumber(String licenceNumber);

    List<Vehicle> findAllByMemberId(Long memberId);
}
