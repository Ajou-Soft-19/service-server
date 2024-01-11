package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.navigation.NaverNavigationService;
import com.ajousw.spring.domain.navigation.OsrmNavigationService;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.web.controller.dto.navigation.NavigationQueryDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NavigationController {

    private final OsrmNavigationService osrmNavigationService;
    private final NaverNavigationService naverNavigationService;

    @PostMapping("/api/navi/osrm/route")
    public ApiResponseJson getOsrmRoute(@RequestBody NavigationQueryDto navigationQueryDto,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserPrinciple userPrinciple) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 파라미터 요청");
        }

        NavigationPathDto navigationPath = osrmNavigationService.getOsrmNavigationPath(userPrinciple.getEmail(),
                navigationQueryDto.getSource(),
                navigationQueryDto.getDest(), navigationQueryDto.getOption(), false);

        return new ApiResponseJson(HttpStatus.OK, navigationPath);
    }

    @PostMapping("/api/navi/naver/route")
    public ApiResponseJson getNaverDriving5(@RequestBody NavigationQueryDto navigationQueryDto,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal UserPrinciple userPrinciple) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 파라미터 요청");
        }

        NavigationPathDto navigationPath = naverNavigationService.getNaverNavigationPath(userPrinciple.getEmail(),
                navigationQueryDto.getSource(),
                navigationQueryDto.getDest(), navigationQueryDto.getOption(), false);

        return new ApiResponseJson(HttpStatus.OK, navigationPath);
    }
}
