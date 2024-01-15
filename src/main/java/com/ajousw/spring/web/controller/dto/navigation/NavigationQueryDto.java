package com.ajousw.spring.web.controller.dto.navigation;

import com.ajousw.spring.domain.member.enums.EnumValidation;
import com.ajousw.spring.domain.navigation.api.Provider;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NavigationQueryDto {

    // TODO: 차량 ID 추가
//    @NotEmpty
//    private Long vehicleId;

    @NotEmpty
    private String source;

    @NotEmpty
    private String dest;

    @EnumValidation(enumClass = Provider.class)
    Provider provider;

    private String option = "noOption";
}