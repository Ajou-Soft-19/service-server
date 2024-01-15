package com.ajousw.spring.domain.navigation.route.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckPointRepository extends JpaRepository<CheckPoint, UUID> {
}
