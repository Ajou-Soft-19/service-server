package com.ajousw.spring.domain.navigation.dto;

import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BroadcastDto {

    Long vehicleId;

    Set<String> targetSession;

    Object data;
}

