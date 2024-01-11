package com.ajousw.spring.web.controller.dto.navigation;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NavigationQueryDto {

    @NotEmpty
    private String source;

    @NotEmpty
    private String dest;

    private String option = "noOption";
}
