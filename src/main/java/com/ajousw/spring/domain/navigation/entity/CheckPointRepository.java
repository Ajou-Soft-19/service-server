package com.ajousw.spring.domain.navigation.entity;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckPointRepository extends JpaRepository<CheckPoint, UUID> {
}
