package com.ajousw.spring.domain.navigation;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.MemberJpaRepository;
import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.repository.CheckPointRepository;
import com.ajousw.spring.domain.navigation.entity.repository.NavigationPathRepository;
import com.ajousw.spring.domain.navigation.entity.repository.PathPointRepository;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import com.ajousw.spring.domain.vehicle.entity.VehicleStatus;
import com.ajousw.spring.domain.vehicle.entity.repository.VehicleRepository;

import java.util.ArrayList;
import java.util.List;

import com.ajousw.spring.domain.vehicle.entity.repository.VehicleStatusRepository;
import com.ajousw.spring.domain.warn.entity.repository.EmergencyEventRepository;
import com.ajousw.spring.web.controller.dto.navigationPath.CheckPointItem;
import com.ajousw.spring.web.controller.dto.navigationPath.NavigationPathWithPointsDto;
import com.ajousw.spring.web.controller.dto.navigationPath.PathPointItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NavigationPathService {
    private final NavigationPathRepository navigationPathRepository;
    private final VehicleRepository vehicleRepository;
    private final PathPointRepository pathPointRepository;
    private final CheckPointRepository checkPointRepository;
    private final VehicleStatusRepository vehicleStatusRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final EmergencyEventRepository emergencyEventRepository;

    public NavigationPathWithPointsDto getNavigationPathWithPointsByVehicleStatusId(String email, String vehicleStatusId) {
        checkRoleAdmin(email);

        VehicleStatus vehicleStatus = vehicleStatusRepository.findVehicleStatusByVehicleStatusId(vehicleStatusId)
                .orElseThrow(() -> {
                    log.info("잘못된 vehicleStatusId.");
                    return new IllegalArgumentException("잘못된 vehicleStatusId 입니다.");
                });

        // is emergency check and is using
        if (vehicleStatus.isEmergencyVehicle() && vehicleStatus.isUsingNavi()) {
            Vehicle vehicle = getVehicleByVehicleId(vehicleStatus.getVehicle().getVehicleId());
            NavigationPath navigationPath = emergencyEventRepository.findNavigationPathIdByVehicleId(vehicle.getVehicleId());

            // path point list
            List<PathPointItem> pathPointItems = getPathPointsByNavigationPath(navigationPath);

            // check point list
            List<CheckPointItem> checkPointItems = getCheckPointsByNavigationPath(navigationPath);
            return new NavigationPathWithPointsDto(navigationPath, vehicle, vehicleStatus, pathPointItems, checkPointItems);
        } else {
            // TODO: 수정해야 함.
            log.info("에러");
            throw new IllegalArgumentException("에러");
        }
    }

    // 권한 체크
    public void checkRoleAdmin(String email) {
        Member member = memberJpaRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.info("존재하지 않는 이메일");
                    return new IllegalArgumentException("존재하지 않는 이메일 유저 입니다.");
                });
        if (!member.hasRole(Role.ROLE_ADMIN)) {
            log.info("관리자 권한 없음");
            throw new IllegalArgumentException("관리자 권한이 없는 유저입니다.");
        }
    }

    public List<CheckPointItem> getCheckPointsByNavigationPath(NavigationPath navigationPath) {
        List<CheckPointItem> result = new ArrayList<CheckPointItem>();
        checkPointRepository.findCheckPointsByNavigationPath(navigationPath)
                .forEach(v -> {
                    result.add(new CheckPointItem(v.getCoordinate().getY(), v.getCoordinate().getX(), v.getPointIndex(), v.getDistance(), v.getDuration()));
                });
        return result;
    }

    public List<PathPointItem> getPathPointsByNavigationPath(NavigationPath navigationPath) {
        List<PathPointItem> result = new ArrayList<PathPointItem>();
        pathPointRepository.findPathPointsByNavigationPath(navigationPath)
                .forEach( v -> {
                    result.add(new PathPointItem(v.getCoordinate().getX(), v.getCoordinate().getY(), v.getPointIndex()));
                });
        return result;
    }

    private NavigationPath getNavigationPathByVehicle(Vehicle vehicle) {
        return navigationPathRepository.findNavigationPathByVehicle(vehicle)
                .orElseThrow(() -> {
                    log.info("해당 vehicle의 navigation path가 등록되어 있지 않음.");
                    return new IllegalArgumentException("해당 vehicle의 navigation path가 등록되어 있지 않습니다.");
                });
    }

    private Vehicle getVehicleByVehicleId(Long vehicleId) {
        return vehicleRepository.findByVehicleId(vehicleId)
                .orElseThrow(() -> {
                    log.info("해당 id값의 vehicle이 존재하지 않음.");
                    return new IllegalArgumentException("해당 id값의 vehicle이 존재하지 않습니다.");
                });
    }
}
