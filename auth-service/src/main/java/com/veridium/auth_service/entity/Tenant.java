package com.veridium.auth_service.entity;

import com.veridium.auth_service.utils.TenantStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@NoArgsConstructor
@Getter
public class Tenant {
    @Id
    private UUID id;
    private String name;
    private String slug;
    private TenantStatus status;
    private String created_at;

    public Tenant(String name, String slug) {
        id = UUID.randomUUID();
        this.status = TenantStatus.LIVE;
        created_at = LocalDateTime.now()
                                  .toString();
        this.slug = slug;
        this.name = name;
    }
}
