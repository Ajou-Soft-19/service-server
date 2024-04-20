package com.ajousw.spring.domain.dashboard;

import lombok.Data;

@Data
public class SupporterCountDto {

    private Region region;

    private Long count;

    public SupporterCountDto(Region region, Long count) {
        this.region = region;
        this.count = count;
    }

}
