package com.ajousw.spring.web.controller.dto.vehicle;

import com.ajousw.spring.domain.member.enums.EnumValidation;
import com.ajousw.spring.domain.vehicle.entity.VehicleType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehiclePutDto {
    //    private String vehicleName;
    private String countryCode;

    private String licenceNumber;

    @EnumValidation(enumClass = VehicleType.class)
    private VehicleType vehicleType;
}
