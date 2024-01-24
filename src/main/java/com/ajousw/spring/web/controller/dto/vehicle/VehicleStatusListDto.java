package com.ajousw.spring.web.controller.dto.vehicle;

import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleStatusListDto {
    public VehicleStatusListDto(Vehicle vehicle) {
        this.licenceNumber = vehicle.getLicenceNumber();
        this.vehicleId = vehicle.getVehicleId();
    }
    @NotEmpty
    private Long vehicleId;

    @NotEmpty
    private String licenceNumber;
}
