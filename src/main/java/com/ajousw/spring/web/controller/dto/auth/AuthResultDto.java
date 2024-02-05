package com.ajousw.spring.web.controller.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResultDto {
    private boolean result;
    private String msg;
}
