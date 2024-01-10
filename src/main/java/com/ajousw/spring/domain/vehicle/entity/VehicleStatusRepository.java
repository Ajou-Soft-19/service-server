package com.ajousw.spring.domain.vehicle.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VehicleStatusRepository extends JpaRepository<VehicleStatus, UUID> {
}
