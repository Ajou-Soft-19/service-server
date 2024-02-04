package com.ajousw.spring.domain.member.repository;

import com.ajousw.spring.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MemberJpaRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findById(Long memberId);

    boolean existsByEmail(String email);

    @Query("select m.roles from Member m where m.email=:email")
    Optional<String> getRoleByEmail(@Param("email") String email);


}
