package com.ajousw.spring.domain.vehicle.entity;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vehicle extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vehicleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 10)
    private String countryCode;

    @Column(length = 30)
    private String licenceNumber;

    @Enumerated
    private VehicleType vehicleType;

    @Builder
    public Vehicle(Member member, String countryCode, String licenceNumber, VehicleType vehicleType) {
        this.member = member;
        this.countryCode = countryCode;
        this.licenceNumber = licenceNumber;
        this.vehicleType = vehicleType;
    }

    public void changeCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void changeLicenceNumber(String licenceNumber) {
        this.licenceNumber = licenceNumber;
    }

    public void changeVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }
}
