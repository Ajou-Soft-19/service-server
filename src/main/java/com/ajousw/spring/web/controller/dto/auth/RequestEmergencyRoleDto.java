package com.ajousw.spring.web.controller.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestEmergencyRoleDto {

    @NotNull
    private Long memberId;

    @NotNull
    private LocalDateTime createdDate;

}
