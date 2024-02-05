package com.ajousw.spring.web.controller.dto.vehicle;

import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VehicleListDto {
    public VehicleListDto(Vehicle vehicle) {
        this.licenceNumber = vehicle.getLicenceNumber();
        this.vehicleId = vehicle.getVehicleId();
        this.vehicleType = vehicle.getVehicleType();
    }
//     @NotEmpty
//     private String vehicleName;
//
//    @NotEmpty
//    private String imgUrl;

    @NotEmpty
    private Long vehicleId;

    @NotEmpty
    private String licenceNumber;

    private VehicleType vehicleType;
}
