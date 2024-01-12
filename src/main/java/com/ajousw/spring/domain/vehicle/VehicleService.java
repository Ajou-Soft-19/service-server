package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.MemberService;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {
    public final VehicleRepository vehicleRepository;
    private final MemberService memberService;

    /* 자동차 정보 수정 */
    @Transactional
    public void updateVehicle(Long id, VehicleCreateDto vehicleCreateDto) {
        Vehicle vehicle = findVehicleByVehicleId(id);
        if (vehicleCreateDto.getVehicleType() != null) {
            vehicle.changeVehicleType(vehicleCreateDto.getVehicleType());
        }

        if (vehicleCreateDto.getLicenceNumber() != null) {
            vehicle.changeLicenceNumber(vehicleCreateDto.getLicenceNumber());
        }

        if (vehicleCreateDto.getCountryCode() != null) {
            vehicle.changeCountryCode(vehicleCreateDto.getCountryCode());
        }
    }

    /* 자동차 전체 삭제 */
    public void removeVehicle(Long vehicleId) {
        try {
            Vehicle vehicle = findVehicleByVehicleId(vehicleId);
            vehicleRepository.delete(vehicle);
        } catch(IllegalArgumentException e) {
            throw e;
        }
    }

    /* 특정 자동차 삭제*/
    public void removeVehicleAll() {
        vehicleRepository.deleteAll();
    }

    /* 자동차 등록 */
    public void createVehicle(VehicleCreateDto vehicleCreateDto, String email) {
        Member member = getMemberId(email);
        validationLicence(vehicleCreateDto.getLicenceNumber());
        Vehicle vehicle = Vehicle.builder()
                .vehicleType(vehicleCreateDto.getVehicleType())
                .countryCode(vehicleCreateDto.getCountryCode())
                .licenceNumber(vehicleCreateDto.getLicenceNumber())
                .phoneNumber(null)
                .member(member)
                .build();

        vehicleRepository.save(vehicle);
    }

    private void validationLicence(String licence) {
        if (vehicleRepository.existsByLicenceNumber(licence)) {
            throw new IllegalArgumentException("이미 등록된 차량입니다.");
        }
    }

    /* 특정 자동차 조회 */
    public Vehicle findVehicleByVehicleId(Long vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId).orElseThrow(() -> {
            log.error("해당 id의 차량의 존재하지 않음.");
            return new IllegalArgumentException("해당 id의 차량이 존재하지 않습니다.");
        });
    }

    /* 사용자의 등록된 모든 자동차 조회 */
    public List<Vehicle> findVehicleAll(String email) {
        Member member = getMemberId(email);
        return vehicleRepository.findAllByMemberId(member.getId());
    }

    private Member getMemberId(String email) {
        return memberService.findByEmail(email);
    }
}

