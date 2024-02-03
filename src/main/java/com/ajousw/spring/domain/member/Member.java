package com.ajousw.spring.domain.member;

import com.ajousw.spring.domain.member.enums.LoginType;
import com.ajousw.spring.domain.member.enums.Role;
import com.ajousw.spring.domain.member.repository.BaseTimeEntity;
import com.ajousw.spring.domain.vehicle.entity.Vehicle;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


@Entity
@Getter
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true, length = 50)
    private String email;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    @Column(length = 50)
    private String username;

    @Column(length = 255)
    private String profileImageUri;

    @Column(length = 30)
    private String phoneNumber;

    private String roles;

    private LocalDateTime lastLoginTime;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private List<Vehicle> vehicles = new ArrayList<>();

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public boolean hasRole(Role role) {
        List<String> parsedRoles = Arrays.stream(roles.split(",")).toList();

        return parsedRoles.contains(role.getRoleName());
    }

    public void addRole(Role role) {
        List<String> parsedRoles = Arrays.stream(roles.split(",")).toList();

        if (parsedRoles.contains(role.getRoleName())) {
            return;
        }

        List<String> newRoles = new ArrayList<>(parsedRoles);
        newRoles.add(role.getRoleName());
        newRoles.sort(Comparator.naturalOrder());

        this.roles = String.join(",", newRoles);
    }

    public void removeRole(Role role) {
        List<String> parsedRoles = Arrays.stream(roles.split(",")).toList();

        if (!parsedRoles.contains(role.getRoleName())) {
            return;
        }

        List<String> newRoles = new ArrayList<>(parsedRoles);
        newRoles.remove(role.getRoleName());
        newRoles.sort(Comparator.naturalOrder());

        this.roles = String.join(",", newRoles);
    }
    
}
