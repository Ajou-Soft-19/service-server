package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.navigation.NavigationPathService;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.vehicle.VehicleStatusService;
import com.ajousw.spring.domain.warn.WarnRecordService;
import com.ajousw.spring.web.controller.dto.navigationPath.NavigationPathRequestDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusCoordinateRequestDto;
import com.ajousw.spring.web.controller.dto.vehicleStatus.VehicleStatusDto;
import com.ajousw.spring.web.controller.dto.warm.WarnInfo;
import com.ajousw.spring.web.controller.dto.warm.WarnListEmergencyRequestDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final VehicleStatusService vehicleStatusService;
    private final WarnRecordService warnRecordService;
    private final NavigationPathService navigationPathService;

    /* 경고를 받은 차량 조회 - 전체 */
//    @PostMapping("/monit/warn-list/all")
//    public ApiResponseJson getVehicleWarmList(@AuthenticationPrincipal UserPrinciple user,
//                                              @RequestBody WarnListRequestDto warnListRequestDto) {
//        List<WarnInfo> result = warnRecordService.getWarnList(user.getEmail(), warnListRequestDto);
//        return new ApiResponseJson(HttpStatus.OK, result);
//    }

    /* 경고를 받은 차량 조회 - emergency_event_id 기준 */
    @PostMapping("/monit/warn-list")
    public ApiResponseJson getVehicleWarnListWithEmergencyEventId(@AuthenticationPrincipal UserPrinciple user,
                                                                  @RequestBody WarnListEmergencyRequestDto warnListEmergencyRequestDto) {
        List<WarnInfo> result = warnRecordService.getWarnList(user.getEmail(),
                warnListEmergencyRequestDto);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 위경도 기반 주행중인 전체 차량 조회 */
    @PostMapping("/monit/vehicle-status")
    public ApiResponseJson getVehicleStatusWithCoordinate(@AuthenticationPrincipal UserPrinciple user,
                                                          @Valid @RequestBody VehicleStatusCoordinateRequestDto vehicleCreateDto) {
        List<VehicleStatusDto> result = vehicleStatusService.getVehicleStatusWithCoordinate(user.getEmail(),
                vehicleCreateDto);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 현재 주행중인 모든 차량 리스트 조회 */
    @GetMapping("/monit/vehicle-status/all")
    public ApiResponseJson getVehicleStatusAll(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleStatusDto> result = vehicleStatusService.findAllVehicleStatus(user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* 주행중인 특정 응급차량 조회 */
//    @GetMapping("/monit/vehicle-status/emergency")
//    public ApiResponseJson getNavigationPathIdByVehicleStatusId(
//            @Valid @RequestParam(value = "vehicleStatusId") String vehicleStatusId,
//            @AuthenticationPrincipal UserPrinciple user) {
//        VehicleStatusNavigationPathDto result = vehicleStatusService.getVehicleStatusEmergencyOne(user.getEmail(),
//                vehicleStatusId);
//        return new ApiResponseJson(HttpStatus.OK, result);
//    }

    /* 응급 상황인 차량의 경로 조회 */
    @PostMapping("/monit/vehicle-status/emergency/route")
    public ApiResponseJson getNavigationPathByVehicleStatusId(@AuthenticationPrincipal UserPrinciple user,
                                                              @Valid @RequestBody NavigationPathRequestDto navigationPathRequestDto) {
        NavigationPathDto navigationPathDto = navigationPathService.getNavigationPathWithPointsByVehicleStatusId(
                user.getEmail(), navigationPathRequestDto.getVehicleStatusId());
        return new ApiResponseJson(HttpStatus.OK, navigationPathDto);
    }

    /* 응급 상황인 차량의 경로 조회 */
    @PostMapping("/monit/vehicle-status/emergency/currentCheckPoint")
    public ApiResponseJson getNavigationPathPointByVehicleStatusId(@AuthenticationPrincipal UserPrinciple user,
                                                                   @Valid @RequestBody NavigationPathRequestDto navigationPathRequestDto) {
        Long currentPathPoint = navigationPathService.getCurrentCheckPoint(
                user.getEmail(), navigationPathRequestDto.getVehicleStatusId());
        return new ApiResponseJson(HttpStatus.OK, Map.of("currentCheckPoint", currentPathPoint));
    }

    /* 주행중인 모든 차량의 경로 조회 - 응급차량 제외*/
//    @GetMapping("/monit/vehicle-status/emergency/all")
//    public ApiResponseJson getNavigationPathAll(@AuthenticationPrincipal UserPrinciple user) {
//        List<VehicleStatusNavigationPathDto> result = vehicleStatusService.getVehicleStatusAllExceptEmergency(
//                user.getEmail());
//        return new ApiResponseJson(HttpStatus.OK, result);
//    }

    /* 주행중인 모든 응급차량의 정보 조회 */
    @GetMapping("/monit/vehicle-status/emergency/all")
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
}
