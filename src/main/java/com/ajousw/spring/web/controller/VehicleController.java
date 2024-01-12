package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.vehicle.VehicleService;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleDto;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleListDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// TODO: DTO 생각해서 추가 및 수정

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;

    @DeleteMapping("")
    public ApiResponseJson deleteVehicleAll() {
        vehicleService.removeVehicleAll();
        return new ApiResponseJson(HttpStatus.OK, "Delete Success");
    }

    @DeleteMapping("/{vehicleId}")
    public ApiResponseJson deleteVehicle(@PathVariable Long vehicleId) {
        try {
            vehicleService.removeVehicle(vehicleId);
            return new ApiResponseJson(HttpStatus.OK, "success");
        } catch(IllegalArgumentException e) {
            return new ApiResponseJson(HttpStatus.BAD_REQUEST, 400,  e.getMessage());
        }
    }

    @PostMapping("")
    public ApiResponseJson setVehicle(@AuthenticationPrincipal UserPrinciple user,
                              @Valid @RequestBody VehicleCreateDto vehicleCreateDto) {
        vehicleService.createVehicle(vehicleCreateDto, user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @PutMapping("/{id}")
    public ApiResponseJson updateVehicle(@PathVariable Long id) {
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ApiResponseJson getVehicle(@PathVariable("id") Long vehicleId) {vehicleService.findVehicleByVehicleId(vehicleId);
         VehicleDto result = new VehicleDto(vehicleService.findVehicleByVehicleId(vehicleId));
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    @GetMapping("/all")
    public ApiResponseJson getVehicleAll(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleListDto> result = vehicleService.findVehicleAll(user.getEmail()).stream()
                .map(v -> new VehicleListDto(v))
                .collect(Collectors.toList());
        return new ApiResponseJson(HttpStatus.OK, result);
    }
}
