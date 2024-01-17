package com.ajousw.spring.domain.auth_request;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import com.ajousw.spring.domain.vehicle.VehicleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_request_id")
    private Long id;

    @Column(unique = true)
    private Long userId;

    @Builder
    public AuthRequest(Long userId) {
        this.userId = userId;
    }
}