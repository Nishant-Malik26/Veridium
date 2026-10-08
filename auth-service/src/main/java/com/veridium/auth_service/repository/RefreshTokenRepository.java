package com.veridium.auth_service.repository;

import com.veridium.auth_service.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshTokenAndUserId(String refreshToken, UUID userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    @Modifying
    @Query("UPDATE RefreshToken r SET r.isRevoked = true WHERE r.userId = :userId AND r.isRevoked = false")
    void revokeAllUserTokens(@Param("userId") UUID userId);
}
