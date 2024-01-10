package com.ajousw.spring.domain.navigation.route.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PathGuideRepository extends JpaRepository<PathGuide, UUID> {
}
