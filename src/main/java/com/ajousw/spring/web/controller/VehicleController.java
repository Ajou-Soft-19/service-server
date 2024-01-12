package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.vehicle.VehicleService;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: DTO 생각해서 추가 및 수정

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    @PostMapping("")
    public Long setVehicle(@AuthenticationPrincipal UserPrinciple user,
                              @Valid @RequestBody VehicleCreateDto vehicleCreateDto) {
        return vehicleService.createVehicle(vehicleCreateDto, user.getEmail()).getVehicleId();
    }

    @PutMapping("")
    public String updateVehicle(@AuthenticationPrincipal UserPrinciple user) {
        return "update Vehicle";
    }

    // TODO: 리턴해줄 DTO 정의해서 수정해야 함.
    @GetMapping("/{id}")
    @ResponseBody
    public String getVehicle(@PathVariable("id") Long vehicleId) {vehicleService.findVehicleByVehicleId(vehicleId);
        Vehicle tmp = vehicleService.findVehicleByVehicleId(vehicleId);
        return "success";
    }

    @GetMapping("/all")
    public List<Vehicle> getVehicleAll(@AuthenticationPrincipal UserPrinciple user) {
        return vehicleService.findVehicleAll(user.getEmail());
    }
}
