package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.vehicle.VehicleService;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleDto;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleListDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
                                      @Valid @RequestBody VehicleCreateDto vehicleCreateDto,
                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        Long vehicleId = vehicleService.createVehicle(vehicleCreateDto, user.getEmail());
        return new ApiResponseJson(HttpStatus.OK, Map.of("vehicleId", vehicleId));
    }

    @PutMapping("/{id}")
    public ApiResponseJson updateVehicle(@PathVariable Long id,
                                         @AuthenticationPrincipal UserPrinciple user,
                                         @Valid @RequestBody VehicleCreateDto vehicleCreateDto,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        vehicleService.updateVehicle(user.getEmail(), id, vehicleCreateDto);
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @GetMapping("/{id}")
    @ResponseBody
    public ApiResponseJson getVehicle(@AuthenticationPrincipal UserPrinciple user,
                                      @PathVariable("id") Long vehicleId) {
        VehicleDto result = vehicleService.getVehicle(user.getEmail(), vehicleId);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    @GetMapping("/all")
    public ApiResponseJson getVehicleAll(@AuthenticationPrincipal UserPrinciple user) {
        List<VehicleListDto> result = vehicleService.findVehicleAllByEmail(user.getEmail()).stream()
                .map(VehicleListDto::new)
                .collect(Collectors.toList());
        return new ApiResponseJson(HttpStatus.OK, result);
    }
}
