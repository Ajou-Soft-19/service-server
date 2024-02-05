package com.ajousw.spring.domain.auth_request;

import com.ajousw.spring.domain.member.Member;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthRequest extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_request_id")
    private Long id;

    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @Enumerated(EnumType.STRING)
    private Role roleType;

    private boolean isPending;

    private boolean isApproved;

    public AuthRequest(Member member, Role role) {
        this.member = member;
        this.roleType = role;
        this.isApproved = false;
        this.isPending = true;
    }

    public void approveRole() {
        this.isPending = false;
        this.isApproved = true;
    }

    public void rejectRole() {
        this.isPending = false;
        this.isApproved = false;
    }
}