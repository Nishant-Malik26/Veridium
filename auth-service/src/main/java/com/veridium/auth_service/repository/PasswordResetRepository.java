package com.veridium.auth_service.repository;

import com.veridium.auth_service.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetOtp, Long> {
    Optional<PasswordResetOtp> findTopByUserIdAndIsUsedFalseOrderByCreatedAtDesc(UUID userId);
}
