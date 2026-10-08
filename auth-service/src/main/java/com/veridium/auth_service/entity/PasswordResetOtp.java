package com.veridium.auth_service.entity;

import com.veridium.auth_service.constants.Constants;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
//import org.springframework.data.annotation.Id;

import java.time.Duration;
import java.time.LocalDateTime;
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
    @Setter
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

    public boolean isExpired() {
        return Duration.between(this.createdAt, LocalDateTime.now()).toMinutes() > Constants.PASSWORD_RESET_EXPIRATION_TIME_IN_MINUTES;
    }
}
