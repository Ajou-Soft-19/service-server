package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.navigation.api.NaverNavigationService;
import com.ajousw.spring.domain.navigation.api.OsrmNavigationService;
import com.ajousw.spring.domain.navigation.dto.NavigationPathDto;
import com.ajousw.spring.web.controller.dto.navigation.NavigationQueryDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
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

    @PostMapping("/api/navi/route")
    public ApiResponseJson getOsrmRoute(@Valid @RequestBody NavigationQueryDto navigationQueryDto,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal UserPrinciple userPrinciple) {
        checkBindingResult(bindingResult);

        NavigationPathDto navigationPath;
        switch (navigationQueryDto.getProvider()) {
            case NAVER -> navigationPath = naverNavigationService.getNaverNavigationPath(userPrinciple.getEmail(),
                    null, navigationQueryDto.getSource(), navigationQueryDto.getDest(),
                    navigationQueryDto.getOption(), false);
            case OSRM -> navigationPath = osrmNavigationService.getOsrmNavigationPath(userPrinciple.getEmail(),
                    null, navigationQueryDto.getSource(), navigationQueryDto.getDest(),
                    navigationQueryDto.getOption(), false);
            default -> throw new IllegalArgumentException("아직 지원하지 않는 API 입니다.");
        }

        return new ApiResponseJson(HttpStatus.OK, navigationPath);
    }

    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 파라미터 요청");
        }
    }
}
