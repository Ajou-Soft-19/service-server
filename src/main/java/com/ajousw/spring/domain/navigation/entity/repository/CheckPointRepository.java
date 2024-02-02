package com.ajousw.spring.domain.navigation.entity.repository;

import com.ajousw.spring.domain.navigation.entity.CheckPoint;

import java.util.List;

import com.ajousw.spring.domain.navigation.entity.NavigationPath;
import com.ajousw.spring.domain.navigation.entity.PointId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckPointRepository extends JpaRepository<CheckPoint, PointId> {

    @Modifying(flushAutomatically = true)
    @Query("delete from CheckPoint cp where cp.navigationPath.naviPathId=:naviPathId")
    void deleteAllByNavigationPathId(@Param("naviPathId") Long naviPathId);

    List<CheckPoint> findCheckPointsByNavigationPath(NavigationPath navigationPath);
}
