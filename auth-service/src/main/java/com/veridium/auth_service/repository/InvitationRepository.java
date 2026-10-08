package com.veridium.auth_service.repository;

import com.veridium.auth_service.entity.UserInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<UserInvitation, Long> {
    Optional<UserInvitation> findByTokenAndTenantId(String token, UUID tenantId);
}
