package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.navigation.EmergencyService;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.navigation.route.NaverNavigationService;
import com.ajousw.spring.domain.navigation.route.OsrmNavigationService;
import com.ajousw.spring.web.controller.dto.navigation.CurrentPointUpdateDto;
import com.ajousw.spring.web.controller.dto.navigation.NavigationQueryDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
public class EmergencyController {
    private final EmergencyService emergencyService;
    private final OsrmNavigationService osrmNavigationService;
    private final NaverNavigationService naverNavigationService;

    @PostMapping("/api/emergency/navi/route")
    public ApiResponseJson getOsrmRoute(@Valid @RequestBody NavigationQueryDto navigationQueryDto,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserPrinciple userPrinciple) {
        checkBindingResult(bindingResult);

        NavigationPathDto navigationPath;
        switch (navigationQueryDto.getProvider()) {
            case NAVER -> navigationPath = naverNavigationService.getNaverNavigationPath(userPrinciple.getEmail(),
                    navigationQueryDto.getSource(), navigationQueryDto.getDest(),
                    navigationQueryDto.getOption(), true, true);
            case OSRM -> navigationPath = osrmNavigationService.getOsrmNavigationPath(userPrinciple.getEmail(),
                    navigationQueryDto.getSource(), navigationQueryDto.getDest(),
                    navigationQueryDto.getOption(), true, true);
            default -> throw new IllegalArgumentException("PROVIDER NOT SUPPORTED");
        }

        return new ApiResponseJson(HttpStatus.OK, navigationPath);
    }

    @GetMapping("/api/emergency/navi/path")
    public ApiResponseJson getSavedNavigationPath(@Param(value = "naviPathId") Long naviPathId,
                                                  @AuthenticationPrincipal UserPrinciple userPrinciple) {
        NavigationPathDto navigationPathDto =
                emergencyService.getNavigationPathById(userPrinciple.getEmail(), naviPathId);

        return new ApiResponseJson(HttpStatus.OK, navigationPathDto);
    }

    @PostMapping("/api/emergency/navi/path/current-position/update")
    public ApiResponseJson updateCurrentPosition(@Valid @RequestBody CurrentPointUpdateDto updateDto,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal UserPrinciple userPrinciple) {
        checkBindingResult(bindingResult);

        emergencyService.updateCurrentPathPoint(userPrinciple.getEmail(),
                updateDto.getNaviPathId(), updateDto.getCurrentPoint());

        return new ApiResponseJson(HttpStatus.OK, "OK");
    }

    @PostMapping("/api/emergency/navi/path/remove")
    public ApiResponseJson removeNavigationPath(@Param(value = "naviPathId") Long naviPathId,
                                                @AuthenticationPrincipal UserPrinciple userPrinciple) {
        emergencyService.removeNavigationPath(userPrinciple.getEmail(), naviPathId);

        return new ApiResponseJson(HttpStatus.OK, "OK");
    }

    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 파라미터 요청");
        }
    }
}
