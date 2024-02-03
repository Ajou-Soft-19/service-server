package com.ajousw.spring.domain.warn.entity.repository;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.warn.entity.EmergencyEvent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmergencyEventRepository extends JpaRepository<EmergencyEvent, Long> {

    boolean existsByEmergencyEventId(Long emergencyEventId);

    @Query("select ee from EmergencyEvent ee where ee.vehicle.vehicleId in :vehicleIds and ee.isActive = true")
    List<EmergencyEvent> findEmergencyEventIdByVehicle(@Param("vehicleIds") List<Long> vehicleIds);

    @Query("select ee.emergencyEventId from EmergencyEvent ee where ee.vehicle = :vehicle and ee.isActive = true")
    Long findEmergencyEventIdByVehicle(@Param("vehicle") Vehicle vehicle);

    @Query("select ee from EmergencyEvent ee where ee.emergencyEventId = :emergencyEventId")
    Optional<EmergencyEvent> findOneByEmergencyEventId(@Param("emergencyEventId") Long emergencyEventId);

    @Query("select ee from EmergencyEvent ee where ee.vehicle = :vehicle and ee.isActive=true order by ee.createdDate desc limit 1")
    Optional<EmergencyEvent> findEmergencyEventByVehicle(@Param("vehicle") Vehicle vehicle);

    @Query("select ee.navigationPath from EmergencyEvent ee where ee.isActive and ee.vehicle.vehicleId = :vehicleId")
    NavigationPath findNavigationPathIdByVehicleId(@Param("vehicleId") Long vehicleId);

    boolean existsByNavigationPath(NavigationPath navigationPath);

    Optional<EmergencyEvent> findByNavigationPath(NavigationPath navigationPath);

    @Query("select e from EmergencyEvent e where e.member=:member and e.vehicle=:vehicle order by e.createdDate desc")
    List<EmergencyEvent> findAllEmergencyEventsOrderByDate(@Param("member") Member member,
                                                           @Param("vehicle") Vehicle vehicle);

    @Query("select e from EmergencyEvent e where e.member=:member and e.vehicle=:vehicle and e.isActive=true order by e.createdDate desc")
    List<EmergencyEvent> findActiveEmergencyEventsOrderByDate(@Param("member") Member member,
                                                              @Param("vehicle") Vehicle vehicle);

    @Query("select e from EmergencyEvent e left join fetch e.warnRecords where e.member=:member and e.vehicle=:vehicle order by e.createdDate desc")
    List<EmergencyEvent> findAllEmergencyEventsOrderByDateFetch(@Param("member") Member member,
                                                                @Param("vehicle") Vehicle vehicle);

    @Query("select e from EmergencyEvent e left join fetch e.warnRecords where e.member=:member and e.vehicle=:vehicle and e.isActive=true order by e.createdDate desc")
    List<EmergencyEvent> findActiveEmergencyEventsOrderByDateFetch(@Param("member") Member member,
                                                                   @Param("vehicle") Vehicle vehicle);
}
