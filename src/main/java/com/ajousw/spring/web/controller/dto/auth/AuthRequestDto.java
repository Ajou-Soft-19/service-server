package com.ajousw.spring.web.controller.dto.auth;

import com.ajousw.spring.domain.auth_request.AuthRequest;
import com.ajousw.spring.domain.member.enums.Role;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthRequestDto {

    private Long authRequestId;

    private String email;

    private Role role;

    private Boolean isPending;

    private Boolean isApproved;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    public AuthRequestDto(AuthRequest authRequest) {
        this.authRequestId = authRequest.getId();
        this.email = authRequest.getMember().getEmail();
        this.role = authRequest.getRoleType();
        this.isApproved = authRequest.isApproved();
        this.isPending = authRequest.isPending();
        this.createdDate = authRequest.getCreatedDate();
        this.modifiedDate = authRequest.getModifiedDate();
    }
}
