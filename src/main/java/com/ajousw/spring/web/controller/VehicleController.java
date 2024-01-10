package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.MemberService;
import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.vehicle.VehicleService;
import com.ajousw.spring.web.controller.dto.vehicle.VehicleCreateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/vehicles")
public class VehicleController {
    private final MemberService memberService;
    private final VehicleService vehicleService;

    @PostMapping("")
    public Long setVehicle(@AuthenticationPrincipal UserPrinciple user,
                              @RequestBody VehicleCreateDto vehicleCreateDto) {
        Optional<Member> member = memberService.findOne(user.getEmail());

        return vehicleService.createVehicle(vehicleCreateDto, member.get()).getVehicleId();
    }

    @PutMapping("")
    public String updateVehicle(@AuthenticationPrincipal UserPrinciple user) {
        return "update Vehicle";
    }

    @GetMapping("")
    public Optional<Member> getVehicle(@AuthenticationPrincipal UserPrinciple user) {
        return memberService.findOne(user.getEmail());
    }
}
