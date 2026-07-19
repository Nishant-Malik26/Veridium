package com.veridium.auth_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "role")
@Getter
@NoArgsConstructor
public class Role {
    @Id
    UUID id;
    @Column(unique = true)
    String name;
    String description;
}
