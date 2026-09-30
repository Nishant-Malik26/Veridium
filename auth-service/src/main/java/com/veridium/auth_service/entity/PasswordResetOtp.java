package com.veridium.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "password-reset-otp")
@NoArgsConstructor
public class PasswordResetOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id")
    private UUID userId;
    private String otpHash;
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;
    private boolean used;

    public PasswordResetOtp(UUID userId, String otpHash, boolean used) {
        this.userId = userId;
        this.otpHash = otpHash;
        this.used = used;
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
