package com.ajousw.spring.web.controller.dto.navigation;

import com.ajousw.spring.domain.member.enums.EnumValidation;
import com.ajousw.spring.domain.navigation.api.provider.factory.Provider;
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

    @EnumValidation(enumClass = Provider.class)
    Provider provider;

    private String option = "noOption";
}
