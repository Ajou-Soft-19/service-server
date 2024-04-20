package com.ajousw.spring.domain.dashboard;

import com.ajousw.spring.domain.navigation.api.NaverReverseGeocodingService;
import com.ajousw.spring.domain.navigation.api.info.rev_geo.RevgeoDto;
import com.ajousw.spring.domain.navigation.entity.MapLocation;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.warn.entity.repository.WarnRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SupporterCountingService {

    private final NavigationPathRepository navigationPathRepository;
    private final SupporterCountRepository supporterCountRepository;
    private final WarnRecordRepository warnRecordRepository;
    private final NaverReverseGeocodingService naverReverseGeocodingService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addEmergencyEventSupporterCount(Long emergencyEventId, Long navigationPathId, LocalDateTime dateTime) {
        NavigationPath navigationPath = navigationPathRepository.findById(navigationPathId)
                .orElse(null);

        if (navigationPath == null) {
            log.error("NavigationPath is not found. navigationPathId: {}", navigationPathId);
            return;
        }

        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();

        Long count = warnRecordRepository.countByEmergencyEventId(emergencyEventId);
        Region region = getRegion(navigationPath);

        SupporterCount supporterCount = SupporterCount.builder()
                .emergencyEventId(emergencyEventId)
                .year(year)
                .month(month)
                .day(day)
                .region(region)
                .supporterCount(count)
                .build();

        supporterCountRepository.save(supporterCount);
        log.info("SupporterCount is saved. emergencyEventId: {}, year: {}, month: {}, day: {}, region: {}, count: {}",
                emergencyEventId, year, month, day, region, count);
    }

    private Region getRegion(NavigationPath navigationPath) {
        MapLocation destLocation = navigationPath.getDestLocation();
        RevgeoDto reverseGeocodingInfo = naverReverseGeocodingService
                .getReverseGeocodingInfo(destLocation.getLatitude(), destLocation.getLongitude());

        log.info("Reverse Geocoding Info: {}", reverseGeocodingInfo.getFullAddress());
        return Region.fromFullAddress(reverseGeocodingInfo.getFullAddress());
    }

    public List<SupporterCountDto> getSupporters(Integer year, Integer month, Integer day) {
        isValidDate(year, month, day);
        if (year != null && month != null && day != null) {
            return supporterCountRepository.getSupportersOfDay(year, month, day);
        } else if (year != null && month != null) {
            return supporterCountRepository.getSupportersOfMonth(year, month);
        } else if (year != null) {
            return supporterCountRepository.getSupportersOfYear(year);
        } else if (year == null && month == null && day == null) {
            int currentYear = LocalDateTime.now().getYear();
            int currentMonth = LocalDateTime.now().getMonthValue();
            return supporterCountRepository.getSupportersOfMonth(currentYear, currentMonth);
        }

        throw new IllegalArgumentException("Invalid date format");
    }

    private void isValidDate(Integer year, Integer month, Integer day) {
        try {
            if (year != null && month != null && day != null) {
                LocalDate.of(year, month, day);
            } else if (year != null && month != null) {
                LocalDate.of(year, month, 1);
            } else if (year != null) {
                LocalDate.of(year, 1, 1);
            }
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date format");
        }
    }

}

