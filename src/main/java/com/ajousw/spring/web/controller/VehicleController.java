package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.vehicle.VehicleService;
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

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    @DeleteMapping("/{vehicleId}")
    public ApiResponseJson deleteVehicle(@AuthenticationPrincipal UserPrinciple user,
                                         @PathVariable Long vehicleId) {
        vehicleService.removeVehicle(user.getEmail(), vehicleId);
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @PostMapping("")
    public ApiResponseJson setVehicle(@AuthenticationPrincipal UserPrinciple user,
                              @Valid @RequestBody VehicleCreateDto vehicleCreateDto) {
        vehicleService.createVehicle(vehicleCreateDto, user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @PutMapping("/{id}")
    public ApiResponseJson updateVehicle(@PathVariable Long id,
                                         @AuthenticationPrincipal UserPrinciple user,
                                         @Valid @RequestBody VehicleCreateDto vehicleCreateDto) {
        vehicleService.updateVehicle(user.getEmail(), id, vehicleCreateDto);
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ApiResponseJson getVehicle(@AuthenticationPrincipal UserPrinciple user,
                                      @PathVariable("id") Long vehicleId) {
        VehicleDto result = new VehicleDto(vehicleService.findVehicleByVehicleId(user.getEmail(), vehicleId));
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    @GetMapping("/all")
    public ApiResponseJson getVehicleAll(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleListDto> result = vehicleService.findVehicleAllByEmail(user.getEmail()).stream()
                .map(VehicleListDto::new)
                .collect(Collectors.toList());
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    @GetMapping("/check/{memberId}/{vehicleId}")
    public void getCheck(@PathVariable Long memberId,
                         @PathVariable Long vehicleId) {
        vehicleService.checkRole(memberId, vehicleId);
    }
}
