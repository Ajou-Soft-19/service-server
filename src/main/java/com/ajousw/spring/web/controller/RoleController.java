package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.MemberService;
import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
public class RoleController {
    private final MemberService memberService;

    /* emergency 권한 요청 */
    @PutMapping("")
    public ApiResponseJson addEmergencyRole(@AuthenticationPrincipal UserPrinciple user) {
        memberService.addRole(user.getEmail(), Role.ROLE_WAIT);
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    @PutMapping("/reject/{id}")
    public ApiResponseJson rejectEmergencyRole(
            @AuthenticationPrincipal UserPrinciple user,
            @PathVariable Long id) {
        // 1. 현재 admin 권한을 가지고 있는지 확인
        // 2. 해당 유저 ROLE_WAIT 삭제
        String result = memberService.removeRole(user.getEmail(), id, Role.ROLE_WAIT);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    @PutMapping("/approve/{id}")
    public ApiResponseJson approveEmergencyRole(
            @AuthenticationPrincipal UserPrinciple user,
            @PathVariable Long id) {
        // 1. 현재 admin 권한을 가지고 있는지 확인
        // 2. 해당 유저 ROLE_WAIT 삭제
        // 3. ROLE_EMERGENCY 추가
        String result = memberService.approveRole(user.getEmail(), id);
        return new ApiResponseJson(HttpStatus.OK, result);
    }
}
