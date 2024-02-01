package com.ajousw.spring.web.controller.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class EmergencyMemberDto {
    public EmergencyMemberDto(Long id, String userName) {
        this.memberId = id;
        this.userName = userName;
    }
    private Long memberId;
    private String userName;
}
