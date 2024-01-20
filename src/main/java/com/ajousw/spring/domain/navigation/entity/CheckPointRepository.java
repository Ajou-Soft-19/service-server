package com.ajousw.spring.domain.navigation.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CheckPointRepository extends JpaRepository<CheckPoint, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("delete from CheckPoint cp where cp.navigationPath.naviPathId=:naviPathId")
    void deleteAllByNavigationPathId(@Param("naviPathId") Long naviPathId);
}
