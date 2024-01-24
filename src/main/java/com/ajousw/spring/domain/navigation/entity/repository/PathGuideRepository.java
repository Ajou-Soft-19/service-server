package com.ajousw.spring.domain.navigation.entity.repository;

import com.ajousw.spring.domain.navigation.entity.PathGuide;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PathGuideRepository extends JpaRepository<PathGuide, UUID> {

    @Modifying(flushAutomatically = true)
    @Query("delete from PathGuide pg where pg.navigationPath.naviPathId=:naviPathId")
    void deleteByNavigationPathId(@Param("naviPathId") Long naviPathId);
}
