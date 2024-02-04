package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.auth_request.AuthRequestService;
import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.MemberService;
import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.web.controller.dto.auth.AuthRequestDto;
import com.ajousw.spring.web.controller.dto.auth.AuthResultDto;
import com.ajousw.spring.web.controller.dto.member.EmergencyMemberDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth/roles")
public class RoleController {
    private final MemberService memberService;
    private final AuthRequestService authRequestService;

    // TODO: API URL 정리 + 트랜잭션....?
    // TODO: 이 함수 삭제해주세용~
    /* test용 admin 권한 등록 api */
    @PostMapping("/admin")
    public ApiResponseJson requestAdminRole(@AuthenticationPrincipal UserPrinciple user) {
        Member member = memberService.findByEmail(user.getEmail());
        memberService.addRole(member.getId(), Role.ROLE_ADMIN);
        return new ApiResponseJson(HttpStatus.OK, member.getRoles());
    }

    /* 응급 차량 권한 가지고 있는 유저 리스트 조회 */
    @GetMapping("/emergency")
    public ApiResponseJson getEergencyRoleList(@AuthenticationPrincipal UserPrinciple user) {
        Member member = memberService.findByEmail(user.getEmail());
        List<EmergencyMemberDto> result = memberService.findEmergencyMember(member);

        return new ApiResponseJson(HttpStatus.OK, result);
    }

    /* emergency 권한 요청 */
    @PostMapping("")
    public ApiResponseJson addEmergencyRole(@AuthenticationPrincipal UserPrinciple user) {
        Member member = memberService.findByEmail(user.getEmail());
        Long requestId = authRequestService.requestEmergencyRole(member);
        return new ApiResponseJson(HttpStatus.OK, Map.of("requestId", requestId));
    }

    @GetMapping("/check-request")
    public ApiResponseJson checkRoleApproved(@AuthenticationPrincipal UserPrinciple user,
                                             @RequestParam(name = "requestId", defaultValue = "-1") Long requestId) {
        Member member = memberService.findByEmail(user.getEmail());
        AuthResultDto authResultDto = authRequestService.checkRoleApproved(member, requestId);

        return new ApiResponseJson(HttpStatus.OK, authResultDto);
    }

    /* emergency 권한 요청한 유저 리스트 */
    @GetMapping("/request")
    public ApiResponseJson getRequestEmergencyRoleList(@AuthenticationPrincipal UserPrinciple user) {
        /* 요청자 admin 권한 있는지 확인 */
        Member member = memberService.findByEmail(user.getEmail());
        List<AuthRequestDto> result = authRequestService.getRequestEmergencyRoleList(member);
        return new ApiResponseJson(HttpStatus.OK, result);
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
        return new ApiResponseJson(HttpStatus.OK, "Added Role");
    }

    // admin이 특정 유저의 emergency 권한을 삭제
    @PostMapping("/delete/{id}")
    public ApiResponseJson deleteEmergency(
            @AuthenticationPrincipal UserPrinciple user,
            @PathVariable Long id) {
        Member member = memberService.findByEmail(user.getEmail());
        String result = memberService.removeRole(member, id, Role.ROLE_EMERGENCY_VEHICLE);
        return new ApiResponseJson(HttpStatus.OK, result);
    }
}
