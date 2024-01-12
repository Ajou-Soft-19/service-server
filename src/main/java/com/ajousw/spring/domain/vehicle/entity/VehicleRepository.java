package com.ajousw.spring.domain.vehicle.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByVehicleId(Long vehicleId);
    boolean existsByLicenceNumber(String licenceNumber);
    List<Vehicle> findAllByMemberId(Long memberId);
}
