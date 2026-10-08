package com.veridium.auth_service.repository;

import com.veridium.auth_service.entity.Role;
import com.veridium.auth_service.entity.Tenant;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {
    boolean existsByTenantAndUserAndRole(Tenant tenant, User user, Role role);

    List<UserRole> findByUser(User user);

    List<UserRole> findByUserIdAndTenantId(UUID userId, UUID tenantId);

    List<UserRole> findByUserEmailAndTenantSlug(
            String email,
            String tenantSlug
    );

    List<UserRole> findByTenantSlugAndUserId(String tenantSlug, UUID userId);

    List<UserRole> findByTenantIdAndRoleId(UUID tenantId, UUID roleId);

}
