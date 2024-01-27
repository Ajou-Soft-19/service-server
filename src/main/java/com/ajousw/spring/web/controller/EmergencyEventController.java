package com.ajousw.spring.web.controller;

import com.ajousw.spring.domain.member.UserPrinciple;
import com.ajousw.spring.domain.warn.EmergencyEventService;
import com.ajousw.spring.domain.warn.entity.dto.EmergencyEventDto;
import com.ajousw.spring.web.controller.dto.emergency.EmergencyEventCreateDto;
import com.ajousw.spring.web.controller.dto.emergency.EmergencyEventEndDto;
import com.ajousw.spring.web.controller.json.ApiResponseJson;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: 로깅 추가
@Slf4j
@RestController
@RequiredArgsConstructor
public class EmergencyEventController {
    private final EmergencyEventService emergencyEventService;

    @PostMapping("/api/emergency/event/register")
    public ApiResponseJson registerEmergencyEvent(@Valid @RequestBody EmergencyEventCreateDto createDto,
                                                  BindingResult bindingResult,
                                                  @AuthenticationPrincipal UserPrinciple user) {
        checkBindingResult(bindingResult);

        EmergencyEventDto emergencyEvent = emergencyEventService.createEmergencyEvent(user.getEmail(), createDto);

        return new ApiResponseJson(HttpStatus.OK, emergencyEvent);
    }

    @PostMapping("/api/emergency/event/end")
    public ApiResponseJson endEmergencyEvent(@Valid @RequestBody EmergencyEventEndDto eventEndDto,
                                             BindingResult bindingResult,
                                             @AuthenticationPrincipal UserPrinciple user) {
        checkBindingResult(bindingResult);

        emergencyEventService.endEmergencyEvent(user.getEmail(), eventEndDto.getEmergencyEventId());

        return new ApiResponseJson(HttpStatus.OK, "Event Ended Successfully");
    }

    @GetMapping("/api/emergency/event")
    public ApiResponseJson getRegisteredEvent(@RequestParam(value = "vehicleId") Long vehicleId,
                                              @RequestParam(value = "onlyActive", defaultValue = "true") Boolean onlyActive,
                                              @RequestParam(value = "includeTarget", defaultValue = "false") Boolean includeTarget,
                                              @AuthenticationPrincipal UserPrinciple user) {

        List<EmergencyEventDto> emergencyEvents = emergencyEventService.getEmergencyEvents(user.getEmail(), vehicleId,
                onlyActive, includeTarget);

        return new ApiResponseJson(HttpStatus.OK, emergencyEvents);
    }

    private void checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("잘못된 파라미터 요청");
        }
    }
}
