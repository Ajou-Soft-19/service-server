package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.navigation.NavigationPathService;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.vehicle.VehicleStatusService;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleStatusListDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    private final VehicleStatusService vehicleStatusService;
    private final NavigationPathService navigationPathService;
    @GetMapping("")
    public String test() {
        return "test";
    }

    /* 모든 현재 응급인 차량 리스트 조회 */
    @PostMapping("/emergency/all")
    public List<VehicleStatusListDto> getVehicleStatusAll(@AuthenticationPrincipal UserPrinciple user) {
        return vehicleStatusService.findVehicleStatusAll();
    }

    /* 특정 응급 차량의 경로 조회 */
    @PostMapping("/emergency/{vehicleId}")
    public Optional<NavigationPath> getNavigationPathByVehicleId(@AuthenticationPrincipal UserPrinciple user,
                                                                @Valid @PathVariable Long vehicleId) {
        return navigationPathService.findNavigationPathByVehicle(vehicleId);
    }


}
