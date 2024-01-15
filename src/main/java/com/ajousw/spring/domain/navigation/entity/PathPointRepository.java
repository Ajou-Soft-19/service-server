package com.ajousw.spring.domain.navigation.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PathPointRepository extends JpaRepository<PathPoint, UUID> {
}
