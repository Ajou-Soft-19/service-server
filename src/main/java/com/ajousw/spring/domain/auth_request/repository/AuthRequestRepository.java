package com.ajousw.spring.domain.auth_request.repository;

import com.ajousw.spring.domain.auth_request.AuthRequest;
import com.ajousw.spring.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AuthRequestRepository extends JpaRepository<AuthRequest, Long> {

    @Query("select ar from AuthRequest ar left join fetch ar.member order by ar.id desc")
    List<AuthRequest> findAllAndOrderById();

    boolean existsById(Long id);

    boolean existsByMemberAndIsPending(Member member, boolean isPending);

}
