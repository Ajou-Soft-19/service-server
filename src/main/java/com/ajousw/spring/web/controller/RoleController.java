package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.auth_request.AuthRequestService;
import com.ajousw.spring.domain.member.Member;
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
@RequestMapping("/api/auth/roles")
public class RoleController {
    private final MemberService memberService;
    private final AuthRequestService authRequestService;


    /* emergency 권한 요청 */
    @PostMapping("")
    public ApiResponseJson addEmergencyRole(@AuthenticationPrincipal UserPrinciple user) {
        System.out.println("post");
        Member member = memberService.findByEmail(user.getEmail());
        authRequestService.addRole(member);
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    // admin이 권한 요청 거절
    @PostMapping("/reject/{id}")
    public ApiResponseJson rejectEmergencyRole(
            @AuthenticationPrincipal UserPrinciple user,
            @PathVariable Long id) {
        Member member = memberService.findByEmail(user.getEmail());
        authRequestService.rejectRole(member, id);
        return new ApiResponseJson(HttpStatus.OK, "success");
    }

    // admin이 권한 요청 승인
    @PostMapping("/approve/{id}")
    public ApiResponseJson approveEmergencyRole(
            @AuthenticationPrincipal UserPrinciple user,
            @PathVariable Long id) {
        Member member = memberService.findByEmail(user.getEmail());
        authRequestService.approveRole(member, id);
        String result = memberService.approveRole(user.getEmail(), id);
        return new ApiResponseJson(HttpStatus.OK, result);
    }

    // admin이 특정 유저의 emergency 권한을 삭제
    @PostMapping("/delete/{id}")
    public ApiResponseJson deleteEmergency(
            @AuthenticationPrincipal UserPrinciple user,
            @PathVariable Long id) {
        Member member = memberService.findByEmail(user.getEmail());
        String result = memberService.removeRole(member, id, Role.ROLE_EMERGENCY_VEHICLE);
        return new ApiResponseJson(HttpStatus.OK, result);
    }}
