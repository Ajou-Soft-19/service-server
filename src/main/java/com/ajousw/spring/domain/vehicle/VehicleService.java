package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {
    public final VehicleRepository vehicleRepository;
    public final MemberJpaRepository memberJpaRepository;

    /* 자동차 등록 */
    public Vehicle createVehicle(VehicleCreateDto vehicleCreateDto, Member member) {
        validationLicence(vehicleCreateDto.getLicenceNumber());
        Vehicle vehicle = Vehicle.builder()
                .vehicleType(vehicleCreateDto.getVehicleType())
                .countryCode(vehicleCreateDto.getCountryCode())
                .licenceNumber(vehicleCreateDto.getLicenceNumber())
                .phoneNumber(null)
                .member(member)
                .build();

        vehicleRepository.save(vehicle);
        return vehicle;
    }

    private void validationLicence(String licence) {
        if (vehicleRepository.existsByLicenceNumber(licence)) {
            throw new IllegalArgumentException("이미 등록된 차량입니다.");
        }
    }

    /* 특정 자동차 조회 */
    public Optional<Vehicle> getVehicle(Long vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId);
    }
}

