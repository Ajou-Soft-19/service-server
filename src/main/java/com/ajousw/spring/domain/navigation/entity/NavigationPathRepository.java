package com.ajousw.spring.domain.navigation.entity;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NavigationPathRepository extends JpaRepository<NavigationPath, Long> {

    @Query("select np from NavigationPath np "
            + "left join fetch np.guides where np.naviPathId=:naviPathId")
    Optional<NavigationPath> findNavigationPathByNaviPathIdFetchGuides(@Param("naviPathId") Long naviPathId);


    @Query("select np from NavigationPath np "
            + "left join fetch np.checkPoints where np.naviPathId=:naviPathId")
    Optional<NavigationPath> findNavigationPathByNaviPathIdFetchCheckPoints(@Param("naviPathId") Long naviPathId);
}
