package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.vehicle.VehicleStatusService;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;
import com.ajousw.spring.domain.warn.WarnRecordService;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleStatusListDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusCoordinateRequestDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusNavigationPathDto;
import com.ajousw.spring.web.controller.dto.warm.WarnListRequestDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final VehicleStatusService vehicleStatusService;
    private final VehicleRepository vehicleRepository;
    private final WarnRecordService warnRecordService;

    @GetMapping("")
    public String test() {
        return "test";
    }

    /* 경고를 받은 차량 조회 */
    @PostMapping("/monit/warn-list")
    public ApiResponseJson getVehicleWarmList(@AuthenticationPrincipal UserPrinciple user,
                                              @RequestBody WarnListRequestDto warnListRequestDto) {
        List<WarnRecord> result = warnRecordService.getWarmListWithTimeAfter(user.getEmail(), warnListRequestDto.getTimeAfter());
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 위경도 기반 주행중인 차량 조회 */
    @PostMapping("/monit/vehicle-status")
    public ApiResponseJson getVehicleStatusWithCoordinate(@AuthenticationPrincipal UserPrinciple user,
                                                          @Valid @RequestBody VehicleStatusCoordinateRequestDto vehicleCreateDto) {
        List<VehicleStatusDto> result = vehicleStatusService.getVehicleStatusWithCoordinate(user.getEmail(), vehicleCreateDto);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 현재 주행중인 모든 응급인 차량 리스트 조회 */
    @GetMapping("/emergency/all")
    public ApiResponseJson getVehicleStatusAll(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleStatusListDto> result = vehicleStatusService.findVehicleStatusAll(user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 주행중인 응급차량 조회 */
    @GetMapping("/monit/vehicle-status/emergency")
    public ApiResponseJson getNavigationPathByVehicleStatusId(@Valid @RequestParam(value="vehicleStatusId") String vehicleStatusId,
                                                              @AuthenticationPrincipal UserPrinciple user) {
        VehicleStatusNavigationPathDto result = vehicleStatusService.getVehicleStatusEmergencyOne(user.getEmail(), vehicleStatusId);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 모든 응급 차량의 경로 조회 */
    @GetMapping("/monit/vehicle-status/emergency/all")
    public ApiResponseJson getNavigationPathAll(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleStatusNavigationPathDto> result = vehicleStatusService.getVehicleStatusAllExceptEmergency(user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 주행중인 모든 응급차량의 정보 조회 */
    @PostMapping("/monit/vehicle-status/emergency/all")
    public ApiResponseJson getEmergencyVehicleOnAction(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleStatusDto> result = vehicleStatusService.getEmergencyVehicleAll(user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 주행중인 모든 차량의 정보 조회 (응급상태의 차량 제외)*/
    @PostMapping("/monit/vehicle-status/all")
    public ApiResponseJson getVehicleStatusExceptEmergency(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleStatusDto> result = vehicleStatusService.getEmergencyVehicleAll(user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, result);
    }


    // TODO: 테스트용
    @PostMapping("/add/vehicle-status")
    public ApiResponseJson addMockDataVehicleStatus() {
        Vehicle vehicle = vehicleRepository.findByVehicleId(Long.valueOf(1)).get();
        vehicleStatusService.addVehicleStatus(vehicle);
        return new ApiResponseJson(HttpStatus.OK, "ok");
    }
}
