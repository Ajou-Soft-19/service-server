package com.ajousw.spring.domain.vehicle.entity.repository;

import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    @Query("select exists (select v.vehicleId from Vehicle v where v.member.id = :memberId and v.vehicleId = :vehicleId)")
    boolean existsByVehicleIdAndMemberId(@Param("vehicleId") Long vehicleId, @Param("memberId") Long memberId);
    Optional<Vehicle> findByVehicleId(Long vehicleId);

    boolean existsByLicenceNumber(String licenceNumber);

    List<Vehicle> findAllByMemberId(Long memberId);
}
