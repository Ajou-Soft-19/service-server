package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleStatusListDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusCoordinateRequestDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusNavigationPathDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleStatusService {
    private final VehicleStatusRepository vehicleStatusRepository;
    private final VehicleRepository vehicleRepository;
    private final NavigationPathRepository navigationPathRepository;
    private final MemberJpaRepository memberJpaRepository;

    /* admin 권한 확인 로직 */
    private void validateRole(String email) {
        Member member = memberJpaRepository.findByEmail(email).get();
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한을 가지고 있지 않음.");
            throw new IllegalArgumentException("관리자 권한을 가지고 있지 않습니다.");
        }
    }

    /* 위경도 기반 주변 주행중인 차량 조회 */
    public List<VehicleStatusDto> getVehicleStatusWithCoordinate(String email, VehicleStatusCoordinateRequestDto vehicleStatusCoordinateRequestDto) {
        validateRole(email);
        return vehicleStatusRepository.findAllWithinRadius(vehicleStatusCoordinateRequestDto.getLongitude(), vehicleStatusCoordinateRequestDto.getLatitude(), vehicleStatusCoordinateRequestDto.getRadius()).stream()
                .map(v -> {
//                    NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(v.getVehicle()).get();
                    return new VehicleStatusDto(v);
                })
                .toList();
    }

    /*주행중인 특정 응급 차량 조회 */
    public VehicleStatusNavigationPathDto getVehicleStatusEmergencyOne(String email, Long vehicleId) {
        validateRole(email);

        Vehicle vehicle = vehicleRepository.findByVehicleId(vehicleId).get();
        VehicleStatus vehicleStatus = vehicleStatusRepository.findVehicleStatusByVehicle(vehicle).get();
        NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(vehicleStatus.getVehicle()).get();

        return new VehicleStatusNavigationPathDto(vehicleStatus, navigationPath);
    }

    /* 응급상태 아닌 주행중인 모든 차량 정보 조회 */
    public List<VehicleStatusNavigationPathDto> getVehicleStatusAllExceptEmergency(String email) {
        validateRole(email);
        List<VehicleStatusNavigationPathDto> result = new ArrayList<VehicleStatusNavigationPathDto>();
        vehicleStatusRepository.findVehicleStatusByIsEmergencyVehicle(false)
                .stream()
                .forEach( v -> {
                    NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(v.getVehicle()).get();
                    result.add(new VehicleStatusNavigationPathDto(v, navigationPath));
                });
        return result;
    }

    /* 모든 주행중인 응급차량 조회 */
    public List<VehicleStatusDto> getEmergencyVehicleAll(String email) {
        validateRole(email);
        List<VehicleStatusDto> result = new ArrayList<VehicleStatusDto>();
        vehicleStatusRepository.findVehicleStatusByIsEmergencyVehicle(true).stream()
                .forEach(v -> {
//                    NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(v.getVehicle()).get();
                    result.add(new VehicleStatusDto(v));
                });

        return result;
    }


    /* 전체 VehicleStatus 조회 */
    public List<VehicleStatusListDto> findVehicleStatusAll(String email) {
        validateRole(email);
        List<VehicleStatusListDto> vehicleStatusListDtoList = new ArrayList<VehicleStatusListDto>();
        // TODO: 나중에 응급상황 컬럼 추가되면, 필터링해서 리턴해줘야 함.
        vehicleStatusRepository.findAll().stream()
                .forEach(v -> vehicleStatusListDtoList.add(new VehicleStatusListDto(v.getVehicle(), v.getVehicleStatusId())));
        return vehicleStatusListDtoList;
    }

    // todo: mock data 추가용으로 삭제 예정
    public void addVehicleStatus(Vehicle vehicle) {
        Coordinate coordinate = new Coordinate(37.447135, 127.167428);
        GeometryFactory gf = new GeometryFactory();
        VehicleStatus data = new VehicleStatus(
                "1",
                vehicle,
                true,
                gf.createPoint(coordinate),
                1.0,
                1.0,
                LocalDateTime.now()
        );
        vehicleStatusRepository.save(data);
    }



}
