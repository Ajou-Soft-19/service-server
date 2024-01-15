package com.ajousw.spring.web.controller.dto.navigation;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CurrentPointUpdateDto {

    @NotNull
    private Long naviPathId;

    @Min(0)
    private Long currentPoint;

}