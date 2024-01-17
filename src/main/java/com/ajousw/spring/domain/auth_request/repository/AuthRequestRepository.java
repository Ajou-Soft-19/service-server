package com.ajousw.spring.domain.auth_request.repository;

import com.ajousw.spring.domain.auth_request.AuthRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRequestRepository extends JpaRepository<AuthRequest, Long> {
    boolean existsById(Long auth_request_id);
    boolean existsByUserId(Long userId);
    void deleteAuthRequestByUserId(Long userId);
}
