package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.AssignRoleDto;
import com.veridium.auth_service.entity.Role;
import com.veridium.auth_service.entity.Tenant;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.exception.role.RoleNotFoundException;
import com.veridium.auth_service.exception.tenant.TenantNotFoundException;
import com.veridium.auth_service.exception.user.UserNotFoundException;
import com.veridium.auth_service.exception.user.UserRoleAlreadyExistsException;
import com.veridium.auth_service.repository.RoleRepository;
import com.veridium.auth_service.repository.TenantRepository;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository, UserRepository userRepository, TenantRepository tenantRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public AssignRoleDto assignRole(UUID userId, UUID tenantId, String roleName) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(UserNotFoundException::new);

        Tenant tenant = tenantRepository.findById(tenantId)
                                        .orElseThrow(TenantNotFoundException::new);

        Role role = roleRepository.findByName(roleName)
                                  .orElseThrow(() -> new RoleNotFoundException(roleName));

        if (!userRoleRepository.existsByTenantAndUserAndRole(tenant, user, role)) {
            UserRole userRole = new UserRole(user, tenant, role);
            UserRole savedUserRole = userRoleRepository.save(userRole);
            return AssignRoleDto.builder()
                                .roleID(savedUserRole.getId())
                                .userId(user.getId())
                                .tenantId(tenant.getId())
                                .userRoleID(savedUserRole.getId())
                                .build();
        } else {
            throw new UserRoleAlreadyExistsException();
        }

    }
}
