package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.domain.navigation.route.NaverNavigationService;
import com.ajousw.spring.domain.navigation.route.NavigationService;
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
public class NavigationController {

    private final NavigationService navigationService;
    private final OsrmNavigationService osrmNavigationService;
    private final NaverNavigationService naverNavigationService;

    @PostMapping("/api/navi/route")
    public ApiResponseJson getOsrmRoute(@Valid @RequestBody NavigationQueryDto navigationQueryDto,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserPrinciple userPrinciple) {
        checkBindingResult(bindingResult);

        NavigationPathDto navigationPath;
        switch (navigationQueryDto.getProvider()) {
            case NAVER -> navigationPath = naverNavigationService.getNaverNavigationPath(userPrinciple.getEmail(),
                    navigationQueryDto.getVehicleId(), navigationQueryDto.getSource(), navigationQueryDto.getDest(),
                    navigationQueryDto.getOption(), false, false);
            case OSRM -> navigationPath = osrmNavigationService.getOsrmNavigationPath(userPrinciple.getEmail(),
                    navigationQueryDto.getVehicleId(), navigationQueryDto.getSource(), navigationQueryDto.getDest(),
                    navigationQueryDto.getOption(), false, false);
            default -> throw new IllegalArgumentException("아직 지원하지 않는 API 입니다.");
        }

        return new ApiResponseJson(HttpStatus.OK, navigationPath);
    }

    @GetMapping("/api/navi/path")
    public ApiResponseJson getSavedNavigationPath(@Param(value = "naviPathId") Long naviPathId,
                                                  @AuthenticationPrincipal UserPrinciple userPrinciple) {
        NavigationPathDto navigationPathDto =
                navigationService.getNavigationPathById(userPrinciple.getEmail(), naviPathId);

        return new ApiResponseJson(HttpStatus.OK, navigationPathDto);
    }

    @PostMapping("/api/navi/path/current-position/update")
    public ApiResponseJson updateCurrentPosition(@Valid @RequestBody CurrentPointUpdateDto updateDto,
                                                 BindingResult bindingResult,
                                                 @AuthenticationPrincipal UserPrinciple userPrinciple) {
        checkBindingResult(bindingResult);

        navigationService.updateCurrentPathPoint(userPrinciple.getEmail(),
                updateDto.getNaviPathId(), updateDto.getCurrentPoint());

        return new ApiResponseJson(HttpStatus.OK, "OK");
    }

    @PostMapping("/api/navi/path/remove")
    public ApiResponseJson removeNavigationPath(@Param(value = "naviPathId") Long naviPathId,
                                                @AuthenticationPrincipal UserPrinciple userPrinciple) {
        navigationService.removeNavigationPath(userPrinciple.getEmail(), naviPathId);

        return new ApiResponseJson(HttpStatus.OK, "OK");
    }

    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 파라미터 요청");
        }
    }
}
