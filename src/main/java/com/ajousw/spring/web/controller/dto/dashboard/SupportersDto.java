package com.ajousw.spring.web.controller.dto.dashboard;

import com.ajousw.spring.domain.dashboard.SupporterCountDto;
import lombok.Getter;

import java.util.List;

@Getter
public class SupportersDto {
    private Long totalEventCount;

    private List<SupporterCountDto> regionSupporters;

    public SupportersDto(Long totalEventCount, List<SupporterCountDto> regionSupporters) {
        this.totalEventCount = totalEventCount;
        this.regionSupporters = regionSupporters;
    }
}
