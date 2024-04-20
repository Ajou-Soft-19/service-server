package com.ajousw.spring.domain.dashboard;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupporterCount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Long emergencyEventId;

    private int year;

    private int month;

    private int day;

    private Long count;

    @Enumerated
    private Region region;

    @Builder
    public SupporterCount(Long emergencyEventId, int year, int month, int day, Region region, Long supporterCount) {
        this.emergencyEventId = emergencyEventId;
        this.year = year;
        this.month = month;
        this.day = day;
        this.region = region;
        this.count = supporterCount;
    }

    
}
