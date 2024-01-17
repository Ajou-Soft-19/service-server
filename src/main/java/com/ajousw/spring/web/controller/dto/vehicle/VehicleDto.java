package com.ajousw.spring.web.controller.dto.vehicle;

import com.ajousw.spring.domain.member.enums.EnumValidation;
import com.ajousw.spring.domain.vehicle.VehicleType;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleDto {
    public VehicleDto(Vehicle vehicle) {
        this.countryCode = vehicle.getCountryCode();
        this.vehicleType = vehicle.getVehicleType();
        this.licenceNumber = vehicle.getLicenceNumber();
        this.vehicleId = vehicle.getVehicleId();
    }
    @NotEmpty
    private Long vehicleId;

    @NotEmpty
    private String countryCode;

    @NotEmpty
    private String licenceNumber;

    @EnumValidation(enumClass = VehicleType.class)
    private VehicleType vehicleType;

    private String phoneNumber;
}
