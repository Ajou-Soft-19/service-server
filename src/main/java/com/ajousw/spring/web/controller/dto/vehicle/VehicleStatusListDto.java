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
    public VehicleStatusListDto(Vehicle vehicle, String vehicleStatusId) {
        this.licenceNumber = vehicle.getLicenceNumber();
        this.vehicleId = vehicle.getVehicleId();
        this.vehicleStatusId = vehicleStatusId;
    }
    @NotEmpty
    private Long vehicleId;

    @NotEmpty
    private String vehicleStatusId;

    @NotEmpty
    private String licenceNumber;
}
