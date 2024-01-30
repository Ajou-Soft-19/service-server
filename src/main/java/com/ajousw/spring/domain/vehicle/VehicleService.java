package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.MemberService;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleDto;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleService {
    public final VehicleRepository vehicleRepository;
    private final MemberService memberService;

    /* 자동차 정보 수정 */
    public void updateVehicle(String email, Long vehicleId, VehicleCreateDto vehicleCreateDto) {
        Vehicle vehicle = findVehicleByVehicleId(vehicleId);
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
        Vehicle vehicle = findVehicleByVehicleId(vehicleId);
        Member member = memberService.findByEmail(email);
        if (member.getVehicles().contains(vehicle)) {
            vehicleRepository.delete(vehicle);
        } else {
            log.info("유저에게 등록되어 있지 않은 차량임.");
            throw new IllegalArgumentException("차량 소유주가 아닙니다.");
        }
    }

    /* 자동차 등록 */
    public Long createVehicle(VehicleCreateDto vehicleCreateDto, String email) {
        Member member = getMemberByEmail(email);
        validationLicence(vehicleCreateDto.getLicenceNumber());
        Vehicle vehicle = Vehicle.builder()
                .vehicleType(vehicleCreateDto.getVehicleType())
                .countryCode(vehicleCreateDto.getCountryCode())
                .licenceNumber(vehicleCreateDto.getLicenceNumber())
                .member(member)
                .build();

        vehicleRepository.save(vehicle);

        return vehicle.getVehicleId();
    }

    private void validationLicence(String licence) {
        if (vehicleRepository.existsByLicenceNumber(licence)) {
            log.info("이미 등록된 차량을 등록하려 함.");
            throw new IllegalArgumentException("이미 등록된 차량입니다.");
        }
    }

    /* 차량 조회 함수 */
    public VehicleDto getVehicle(String email, Long vehicleId) {
        Vehicle vehicle = this.findVehicleByVehicleId(vehicleId);
        Member member = memberService.findByEmail(email);
        this.checkRole(member, vehicle);
        return new VehicleDto(vehicle);
    }

    /* 차량 id로 자동차 조회 */
    public Vehicle findVehicleByVehicleId(Long vehicleId) {
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
    public List<Vehicle> findVehicleAllByMemberId(Long memberId) {
        return vehicleRepository.findAllByMemberId(memberId);
    }


    // 권한 있는지 체크하는 메소드
    public void checkRole(Member member, Vehicle vehicle) {
        if (!Objects.equals(member.getId(), vehicle.getMember().getId())) {
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

