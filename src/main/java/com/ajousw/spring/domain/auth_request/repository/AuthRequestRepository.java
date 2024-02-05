package com.ajousw.spring.domain.auth_request.repository;

import com.ajousw.spring.domain.auth_request.AuthRequest;
import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuthRequestRepository extends JpaRepository<AuthRequest, Long> {

    @Query("select ar from AuthRequest ar left join fetch ar.member order by ar.id desc")
    List<AuthRequest> findAllAndOrderById();

    boolean existsById(Long id);

    boolean existsByMemberAndIsPending(Member member, boolean isPending);

    @Query("select ar.id from AuthRequest ar where ar.member=:member and ar.roleType=:roleType and ar.isPending=true")
    Optional<Long> findByMemberAndRoleTypeAndIsPending(@Param("member") Member member,
                                                       @Param("roleType") Role role);

}
