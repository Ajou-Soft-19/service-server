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
    public void updateVehicle(String email, Long vehicleId, VehicleCreateDto vehicleCreateDto) {
        Vehicle vehicle = findVehicleByVehicleId(email, vehicleId);
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

    /* 특정 자동차 삭제 */
    public void removeVehicle(String email, Long vehicleId) {
        Vehicle vehicle = findVehicleByVehicleId(email, vehicleId);
        vehicleRepository.delete(vehicle);
    }

    /* 자동차 등록 */
    public void createVehicle(VehicleCreateDto vehicleCreateDto, String email) {
        Member member = getMemberByEmail(email);
        validationLicence(vehicleCreateDto.getLicenceNumber());
        Vehicle vehicle = Vehicle.builder()
                .vehicleType(vehicleCreateDto.getVehicleType())
                .countryCode(vehicleCreateDto.getCountryCode())
                .licenceNumber(vehicleCreateDto.getLicenceNumber())
                .member(member)
                .build();

        vehicleRepository.save(vehicle);
    }

    private void validationLicence(String licence) {
        if (vehicleRepository.existsByLicenceNumber(licence)) {
            log.info("이미 등록된 차량을 등록하려 함.");
            throw new IllegalArgumentException("이미 등록된 차량입니다.");
        }
    }

    /* 특정 자동차 조회 */
    public Vehicle findVehicleByVehicleId(String email, Long vehicleId) {
        Member member = getMemberByEmail(email);
        this.checkRole(member.getId(), vehicleId);
        return vehicleRepository.findByVehicleId(vehicleId).orElseThrow(() -> {
            log.info("해당 id의 차량의 존재하지 않음.");
            return new IllegalArgumentException("해당 id의 차량이 존재하지 않습니다.");
        });
    }

    /* 사용자의 등록된 모든 자동차 조회 이메일로 */
    public List<Vehicle> findVehicleAllByEmail(String email) {
        Member member = getMemberByEmail(email);
        return vehicleRepository.findAllByMemberId(member.getId());
    }

    /* 사용자의 등록된 모든 자동차 조회 아이디로 */
    public List<Vehicle> findVehicleAllByEmail(Long memberId) {
        Member member = getMemberById(memberId);
        return vehicleRepository.findAllByMemberId(member.getId());
    }


    // 권한 있는지 체크하는 메소드
    public void checkRole(Long memberId, Long vehicleId) {
        if (!vehicleRepository.findByVehicleId(vehicleId).get().getMember().getId().equals(memberId)) {
            log.info("사용자에게 권한이 없는 차량");
            throw new IllegalArgumentException("사용자에게 권한이 없는 차량입니다.");
        }
    }

    private Member getMemberById(Long memberId) {
        return memberService.findByMemberId(memberId);
    }

    private Member getMemberByEmail(String email) {
        return memberService.findByEmail(email);
    }
}

