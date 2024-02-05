package com.ajousw.spring.web.controller.dto.navigationPath;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PathPointItem {
    private Double latitude;
    private Double longitude;
    private Long index;
}
