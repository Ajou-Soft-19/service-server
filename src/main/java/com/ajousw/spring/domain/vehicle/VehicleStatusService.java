package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.domain.warn.entity.EmergencyEvent;
import com.ajousw.spring.domain.warn.entity.repository.EmergencyEventRepository;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusCoordinateRequestDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusNavigationPathDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleStatusService {
    private final VehicleStatusRepository vehicleStatusRepository;
    private final NavigationPathRepository navigationPathRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final EmergencyEventRepository emergencyEventRepository;

    /* admin 권한 확인 로직 */
    private void validateRole(String email) {
        Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("해당 이메일을 가진 유저가 존재하지 않음.");
                    return new IllegalArgumentException("해당 이메일을 가진 유저가 존재하지 않습니다.");
                });
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한을 가지고 있지 않음.");
            throw new IllegalArgumentException("관리자 권한을 가지고 있지 않습니다.");
        }
    }

    /* 위경도 기반 주변 주행중인 차량 조회 */
    public List<VehicleStatusDto> getVehicleStatusWithCoordinate(String email,
                                                                 VehicleStatusCoordinateRequestDto vehicleStatusCoordinateRequestDto) {
        validateRole(email);
        List<VehicleStatus> vehicleStatuses = vehicleStatusRepository.findAllWithinRadiusFetch(vehicleStatusCoordinateRequestDto.getLongitude(),
                vehicleStatusCoordinateRequestDto.getLatitude(), vehicleStatusCoordinateRequestDto.getRadius());

        // 응급 차량들의 vehicleId
        List<Long> emergencyVehicleIds = vehicleStatuses.stream().filter(VehicleStatus::isEmergencyVehicle).map(v -> v.getVehicle().getVehicleId()).toList();
        System.out.println(emergencyVehicleIds);

        // 응급 상태인 응급 차량의 emergencyEventId 뽑아냄
        // key: vehicleId
        // value: emergencyEventId
        List<EmergencyEvent> emergencyEvents = emergencyEventRepository.findEmergencyEventIdByVehicle(emergencyVehicleIds);
        Map<Long, Long> emergencyEventMap = new HashMap<Long, Long>();
        emergencyEvents.forEach(e -> {
            emergencyEventMap.put(e.getVehicle().getVehicleId(), e.getEmergencyEventId());
        });

        return vehicleStatuses
                .stream()
                .map( v -> {
                    return insertEmergencyEventId(v, emergencyEventMap);
                }).toList();
    }

    private VehicleStatusDto insertEmergencyEventId(VehicleStatus v, Map<Long, Long> emergencyEvents) {
        if (v.getVehicle() != null) {
            System.out.println(emergencyEvents.get(v.getVehicle().getVehicleId()));
        }
        if ( v.getVehicle() != null && emergencyEvents.get(v.getVehicle().getVehicleId()) != null) {
            return new VehicleStatusDto(v, emergencyEvents.get(v.getVehicle().getVehicleId()));
        } else {
            return new VehicleStatusDto(v);
        }
    }

    /*주행중인 특정 응급 차량 조회 */
    public VehicleStatusNavigationPathDto getVehicleStatusEmergencyOne(String email, String vehicleStatusId) {
        validateRole(email);

        VehicleStatus vehicleStatus = vehicleStatusRepository.findVehicleStatusByVehicleStatusId(vehicleStatusId)
                .orElseThrow(() -> {
                    log.info("해당 id값의 vehicleStatus가 존재하지 않음");
                    return new IllegalArgumentException("해당 id값의 vehicleStatus가 존재하지 않습니다.");
                });

        if (!vehicleStatus.isEmergencyVehicle()) {
            log.info("응급 차량이 아님");
            throw new IllegalArgumentException("응급차량이 아닙니다.");
        }

        NavigationPath navigationPath = emergencyEventRepository.findNavigationPathIdByVehicleId(
                vehicleStatus.getVehicle().getVehicleId());

        return new VehicleStatusNavigationPathDto(vehicleStatus, navigationPath);
    }

    /* 응급상태 아닌 주행중인 모든 차량 정보 조회 */
    public List<VehicleStatusNavigationPathDto> getVehicleStatusAllExceptEmergency(String email) {
        validateRole(email);
        List<VehicleStatusNavigationPathDto> result = new ArrayList<VehicleStatusNavigationPathDto>();
        vehicleStatusRepository.findAllEmergencyVehicle()
                .forEach(v -> {
                    NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(v.getVehicle())
                            .get();
                    result.add(new VehicleStatusNavigationPathDto(v, navigationPath));
                });
        return result;
    }

    /* 모든 주행중인 응급차량 조회 */
    public List<VehicleStatusDto> getEmergencyVehicleAll(String email) {
        validateRole(email);
        List<VehicleStatusDto> result = new ArrayList<VehicleStatusDto>();
        vehicleStatusRepository.findAllEmergencyVehicle()
                .forEach(v -> {
                    result.add(new VehicleStatusDto(v, emergencyEventRepository.findEmergencyEventIdByVehicle(v.getVehicle())));
                });

        return result;
    }

    /* 전체 active emergency vehicleStatus 조회 */
//    public List<VehicleStatusDto> findAllActiveEmergencyVehicle(String email) {
//        validateRole(email);
//        return vehicleStatusRepository.findAllEmergencyVehicle()
//                .stream().map(VehicleStatusDto::new)
//                .toList();
//    }

    /* 전체 VehicleStatus 조회 */
    public List<VehicleStatusDto> findAllVehicleStatus(String email) {
        validateRole(email);
        return vehicleStatusRepository.findAll()
                .stream().map(VehicleStatusDto::new)
                .toList();
    }
}
