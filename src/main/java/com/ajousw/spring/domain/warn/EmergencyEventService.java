package com.ajousw.spring.domain.warn;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;
import com.ajousw.spring.domain.warn.entity.EmergencyEvent;
import com.ajousw.spring.domain.warn.entity.WarnRecord;
import com.ajousw.spring.domain.warn.entity.dto.EmergencyEventDto;
import com.ajousw.spring.domain.warn.entity.dto.WarnRecordDto;
import com.ajousw.spring.domain.warn.entity.repository.EmergencyEventRepository;
import com.ajousw.spring.domain.warn.entity.repository.WarnRecordRepository;
import com.ajousw.spring.web.controller.dto.emergency.EmergencyEventCreateDto;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// TODO: 경고 대상이 된 차량 조회 로직 구현
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EmergencyEventService {

    private final EmergencyEventRepository emergencyEventRepository;
    private final WarnRecordRepository warnRecordRepository;
    private final NavigationPathRepository navigationPathRepository;
    private final VehicleRepository vehicleRepository;
    private final MemberJpaRepository memberRepository;

    public EmergencyEventDto createEmergencyEvent(String email, EmergencyEventCreateDto eventCreateDto) {
        Member member = findMemberByEmail(email);
        Vehicle vehicle = findVehicleById(eventCreateDto.getVehicleId());
        NavigationPath path = findNavigationPathById(eventCreateDto.getNavigationPathId());

        checkVehicleOwner(member, vehicle);
        checkPathOwner(member, path);
        checkVehiclePath(vehicle, path);
        if (emergencyEventRepository.existsByNavigationPath(path)) {
            throw new IllegalArgumentException("NavigationPath is Already Registered as Emergency Event");
        }

        EmergencyEvent emergencyEvent = new EmergencyEvent(path, member, vehicle);
        emergencyEventRepository.save(emergencyEvent);

        return createEmergencyEventDto(emergencyEvent, false);
    }

    public void endEmergencyEvent(String email, Long emergencyEventId) {
        Member member = findMemberByEmail(email);
        EmergencyEvent event = findEmergencyEventById(emergencyEventId);

        checkEventOwner(member, event);
        event.endEvent();
    }

    // 내부 로깅용
    public void addWarnRecord(EmergencyEvent emergencyEvent, Long checkPointIndex, List<Vehicle> targetList) {
        List<WarnRecord> newRecord = targetList.stream()
                .map(t -> new WarnRecord(emergencyEvent, checkPointIndex, t))
                .toList();

        warnRecordRepository.saveAll(newRecord);
    }

    @Transactional(readOnly = true)
    public List<EmergencyEventDto> getEmergencyEvents(String email, Long vehicleId, boolean onlyActive) {
        Member member = findMemberByEmail(email);
        Vehicle vehicle = findVehicleById(vehicleId);
        checkVehicleOwner(member, vehicle);

        List<EmergencyEvent> emergencyEvents = onlyActive
                ? emergencyEventRepository.findActiveEmergencyEventsOrderByDate(member, vehicle)
                : emergencyEventRepository.findAllEmergencyEventsOrderByDate(member, vehicle);

        return toEmergencyEventDtoList(emergencyEvents, false);
    }

    private List<EmergencyEventDto> toEmergencyEventDtoList(List<EmergencyEvent> emergencyEvents,
                                                            boolean includeWarnRecords) {
        return emergencyEvents.stream()
                .map(e -> createEmergencyEventDto(e, includeWarnRecords))
                .toList();
    }

    private EmergencyEventDto createEmergencyEventDto(EmergencyEvent emergencyEvent, boolean includeWarnRecords) {
        List<WarnRecordDto> warnRecordDtos =
                includeWarnRecords ? createWarnRecordDto(emergencyEvent.getWarnRecords()) : List.of();

        return EmergencyEventDto.builder()
                .emergencyEventId(emergencyEvent.getEmergencyEventId())
                .navigationPathId(emergencyEvent.getNavigationPath().getNaviPathId())
                .issuerEmail(emergencyEvent.getMember().getEmail())
                .isActive(emergencyEvent.getIsActive())
                .createdDate(emergencyEvent.getCreatedDate())
                .endedDate(emergencyEvent.getEndedDate())
                .warnRecordDtos(warnRecordDtos)
                .build();
    }

    private List<WarnRecordDto> createWarnRecordDto(List<WarnRecord> warnRecords) {
        Map<Long, List<WarnRecord>> warnRecordGroupByEventId = warnRecords.stream()
                .collect(Collectors.groupingBy(wr -> wr.getWarnRecordId().getCheckPointIndex()));

        return warnRecordGroupByEventId.entrySet().stream()
                .map(entry -> new WarnRecordDto(entry.getKey(), entry.getValue().stream()
                        .map(wr -> wr.getWarnRecordId().getVehicleId())
                        .collect(Collectors.toList())))
                .toList();
    }

    private void checkEventOwner(Member member, EmergencyEvent event) {
        if (event.getMember().getId() != member.getId()) {
            throw new IllegalArgumentException("Not Owner of EmergencyEvent");
        }
    }

    private void checkVehicleOwner(Member member, Vehicle vehicle) {
        if (!vehicle.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Not Owner of vehicle");
        }
    }

    private void checkPathOwner(Member member, NavigationPath navigationPath) {
        if (!navigationPath.getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("Not Owner of Navigation Path");
        }
    }

    private void checkVehiclePath(Vehicle vehicle, NavigationPath navigationPath) {
        if (!navigationPath.getVehicle().getVehicleId().equals(vehicle.getVehicleId())) {
            throw new IllegalArgumentException("Vehicle is Not Owner of Navigation Path");
        }
    }

    private EmergencyEvent findEmergencyEventById(Long emergencyEventId) {
        return emergencyEventRepository.findById(emergencyEventId).orElseThrow(() -> {
            log.info("존재하지 않는 응급 상황");
            return new IllegalArgumentException("No Such Emergency Event");
        });
    }

    private NavigationPath findNavigationPathById(Long naviPathId) {
        return navigationPathRepository.findById(naviPathId).orElseThrow(() -> {
            log.info("존재하지 않는 네비게이션 경로");
            return new IllegalArgumentException("No Such Navigation Path");
        });
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> {
            log.error("계정이 존재하지 않음");
            return new IllegalArgumentException("No Such Member");
        });
    }

    private Vehicle findVehicleById(Long vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId).orElseThrow(() -> {
            log.info("차량 ID가 존재하지 않습니다.");
            return new IllegalArgumentException("No Such Vehicle");
        });
    }
}
