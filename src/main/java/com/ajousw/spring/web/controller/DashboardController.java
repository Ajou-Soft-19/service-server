package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.dashboard.SupporterCountDto;
import com.ajousw.spring.domain.dashboard.SupporterCountingService;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final SupporterCountingService supporterCountingService;

    @GetMapping("/api/supporter/count")
    public ApiResponseJson getSupporterCount(@RequestParam(value = "year", required = false) Integer year,
                                             @RequestParam(value = "month", required = false) Integer month,
                                             @RequestParam(value = "day", required = false) Integer day) {
        List<SupporterCountDto> supporters = supporterCountingService.getSupporters(year, month, day);

        return new ApiResponseJson(HttpStatus.OK, supporters);
    }

}
