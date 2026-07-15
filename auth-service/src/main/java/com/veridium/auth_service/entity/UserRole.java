package com.veridium.auth_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_role")
@NoArgsConstructor
@Getter
public class UserRole {
    @Id
    UUID id;
    @ManyToOne
    User user;
    @ManyToOne
    Tenant tenant;
    @ManyToOne
    Role role;

    public UserRole(User user, Tenant tenant, Role role) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.tenant = tenant;
        this.role = role;
    }
}
