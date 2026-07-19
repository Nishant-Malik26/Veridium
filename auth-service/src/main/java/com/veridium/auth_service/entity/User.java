package com.veridium.auth_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User {
    @Id
    private UUID id;
    private String first_name;
    private String last_name;
    private String email;
    private String password_hash;
    private boolean enabled;

    public User(String first_name, String last_name, String email, String password_hash) {
        id = UUID.randomUUID();
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password_hash = password_hash;
        enabled = true;
    }
}
