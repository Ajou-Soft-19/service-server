package com.ajousw.spring.domain.navigation.route.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NavigationPathRepository extends JpaRepository<NavigationPath, Long> {

    @Query("select np from NavigationPath np "
            + "left join fetch np.guides " + "where np.naviPathId=:naviPathId")
    Optional<NavigationPath> findNavigationPathByNaviPathIdFetchGuides(@Param("naviPathId") Long naviPathId);
}
