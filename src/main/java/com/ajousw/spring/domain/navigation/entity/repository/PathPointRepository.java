package com.ajousw.spring.domain.navigation.entity.repository;

import com.ajousw.spring.domain.navigation.entity.PathPoint;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PathPointRepository extends JpaRepository<PathPoint, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("delete from PathPoint p where p.navigationPath.naviPathId=:naviPathId")
    void deleteByNavigationPathId(@Param("naviPathId") Long naviPathId);

}
