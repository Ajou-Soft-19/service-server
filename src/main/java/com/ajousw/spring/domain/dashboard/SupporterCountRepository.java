package com.ajousw.spring.domain.dashboard;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupporterCountRepository extends JpaRepository<SupporterCount, Long> {

    @Query("select new com.ajousw.spring.domain.dashboard.SupporterCountDto(s.region, sum(s.count)) " +
            "from SupporterCount s where s.year = :year and s.month = :month group by s.region")
    List<SupporterCountDto> getSupportersOfMonth(@Param("year") int year, @Param("month") int month);

    @Query("select new com.ajousw.spring.domain.dashboard.SupporterCountDto(s.region, sum(s.count)) " +
            "from SupporterCount s where s.year = :year and s.month = :month and s.day = :day group by s.region")
    List<SupporterCountDto> getSupportersOfDay(@Param("year") int year, @Param("month") int month, @Param("day") int day);


    @Query("select new com.ajousw.spring.domain.dashboard.SupporterCountDto(s.region, sum(s.count)) " +
            "from SupporterCount s where s.year = :year group by s.region")
    List<SupporterCountDto> getSupportersOfYear(@Param("year") int year);


    @Query("select count(s) from SupporterCount s where s.year = :year and s.month = :month and s.day = :day")
    Long countEmergencyEventsOfDay(@Param("year") int year, @Param("month") int month, @Param("day") int day);

    @Query("select count(s) from SupporterCount s where s.year = :year and s.month = :month")
    Long countEmergencyEventsOfMonth(@Param("year") int year, @Param("month") int month);

    @Query("select count(s) from SupporterCount s where s.year = :year")
    Long countEmergencyEventsOfYear(@Param("year") int year);
}
