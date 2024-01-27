package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatusRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleStatusListDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusEmergencyDto;
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

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleStatusService {
    private final VehicleStatusRepository vehicleStatusRepository;
    private final NavigationPathRepository navigationPathRepository;

    /*주행중인 특정 응급 차량 조회 */
    public VehicleStatusNavigationPathDto getVehicleStatusEmergencyOne(String vehicleStatusId) {
        VehicleStatus vehicleStatus = vehicleStatusRepository.findVehicleStatusByVehicleStatusId(vehicleStatusId).get();
        NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(vehicleStatus.getVehicle()).get();

        return new VehicleStatusNavigationPathDto(vehicleStatus,navigationPath);
    }

    /* 응급상태 아닌 주행중인 모든 차량 정보 조회 */
    public List<VehicleStatusNavigationPathDto> getVehicleStatusAllExceptEmergency() {
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
    public List<VehicleStatusEmergencyDto> getEmergencyVehicleAll() {
        List<VehicleStatusEmergencyDto> result = new ArrayList<VehicleStatusEmergencyDto>();
        vehicleStatusRepository.findVehicleStatusByIsEmergencyVehicle(true).stream()
                .forEach(v -> {
                    NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(v.getVehicle()).get();
                    result.add(new VehicleStatusEmergencyDto(v, navigationPath));
                });

        return result;
    }


    /* 전체 VehicleStatus 조회 */
    public List<VehicleStatusListDto> findVehicleStatusAll() {
        List<VehicleStatusListDto> vehicleStatusListDtoList = new ArrayList<VehicleStatusListDto>();
        // TODO: 나중에 응급상황 컬럼 추가되면, 필터링해서 리턴해줘야 함.
        vehicleStatusRepository.findAll().stream()
                .forEach(v -> vehicleStatusListDtoList.add(new VehicleStatusListDto(v.getVehicle())));
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
