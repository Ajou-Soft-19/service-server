package com.ajousw.spring.web.controller.dto.vehicle;

import com.ajousw.spring.domain.member.enums.EnumValidation;
import com.ajousw.spring.domain.vehicle.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleCreateDto {

    @NotEmpty
    private String countryCode;

    @NotEmpty
    private String licenceNumber;

    @EnumValidation(enumClass = VehicleType.class)
    private VehicleType vehicleType;

    private String phoneNumber;
}
