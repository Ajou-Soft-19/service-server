package com.ajousw.spring.web.controller.dto.emergency;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmergencyEventCreateDto {

    @NotNull
    private Long navigationPathId;

    @NotNull
    private Long vehicleId;

}
