package com.veridium.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
//import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false,name = "refresh_token")
    private String refreshToken;
    @Column(name ="user_id")
    private UUID userId;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
    private String device;
    private String ipAddress;
    private boolean revoked;

    public RefreshToken(String refreshToken, UUID userId, String device, String ipAddress, boolean revoked) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.device = device;
        this.ipAddress = ipAddress;
        this.revoked = revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

}
