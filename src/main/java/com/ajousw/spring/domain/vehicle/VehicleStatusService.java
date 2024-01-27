package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatusRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleStatusListDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusEmergencyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleStatusService {
    private final VehicleStatusRepository vehicleStatusRepository;
    private final NavigationPathRepository navigationPathRepository;

    /* 모든 주행중인 응급차량 조회 */
    public List<VehicleStatusEmergencyDto> getEmergencyVehicleAll() {
        List<VehicleStatusEmergencyDto> vehicleStatusEmergencyDtoList = new ArrayList<VehicleStatusEmergencyDto>();
        vehicleStatusRepository.findVehicleStatusByIsEmergencyVehicle(true).stream()
                .forEach(v -> {
                    NavigationPath navigationPath = navigationPathRepository.findNavigationPathByVehicle(v.getVehicle()).get();
                    vehicleStatusEmergencyDtoList.add(new VehicleStatusEmergencyDto(v, navigationPath));
                });

        return vehicleStatusEmergencyDtoList;
    }


    /* 전체 VehicleStatus 조회 */
    public List<VehicleStatusListDto> findVehicleStatusAll() {
        List<VehicleStatusListDto> vehicleStatusListDtoList = new ArrayList<VehicleStatusListDto>();
        // TODO: 나중에 응급상황 컬럼 추가되면, 필터링해서 리턴해줘야 함.
        vehicleStatusRepository.findAll().stream()
                .forEach(v -> vehicleStatusListDtoList.add(new VehicleStatusListDto(v.getVehicle())));
        return vehicleStatusListDtoList;
    }



}
