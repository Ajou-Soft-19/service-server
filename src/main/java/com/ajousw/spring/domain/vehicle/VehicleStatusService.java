package com.ajousw.spring.domain.vehicle;

import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleStatusListDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class VehicleStatusService {
    private final VehicleStatusRepository vehicleStatusRepository;

    // 전체 VehicleStatus 조회
    public List<VehicleStatusListDto> findVehicleStatusAll() {
        List<VehicleStatusListDto> vehicleStatusListDtoList = new ArrayList<VehicleStatusListDto>();
        // TODO: 나중에 응급상황 컬럼 추가되면, 필터링해서 리턴해줘야 함.
        vehicleStatusRepository.findAll().stream()
                .forEach(v -> vehicleStatusListDtoList.add(new VehicleStatusListDto(v.getVehicle())));
        return vehicleStatusListDtoList;
    }

}
