package com.veridium.auth_service.service;

import com.veridium.auth_service.constants.Constants;
import com.veridium.auth_service.dto.*;
import com.veridium.auth_service.entity.Tenant;
import com.veridium.auth_service.repository.TenantRepository;
import com.veridium.auth_service.utils.GenerateSlug;
import org.springframework.stereotype.Service;

@Service
public class TenantService {
    private final TenantRepository tenantRepository;
    private final UserService userService;
    private final GenerateSlug generateSlug;
    private final RoleService roleService;

    public TenantService(TenantRepository repository, UserService userService, GenerateSlug generateSlug, RoleService roleService) {
        this.tenantRepository = repository;
        this.userService = userService;
        this.generateSlug = generateSlug;
        this.roleService = roleService;
    }

    public RegistrationResponse createTenant(RegistrationRequest registrationRequest) {
        String slug = generateSlug.generateUniqueSlug(registrationRequest.getCompanyName());
        Tenant tenant = new Tenant(registrationRequest.getCompanyName(), slug);
        Tenant savedTenant = tenantRepository.save(tenant);
        UserCreationRequest userCreationRequest = UserCreationRequest.builder()
                                                                     .email(registrationRequest.getEmail())
                                                                     .tenantSlug(tenant.getSlug())
                                                                     .first_name(registrationRequest.getFirstName())
                                                                     .last_name(registrationRequest.getLastName())
                                                                     .password(registrationRequest.getPassword())
                                                                     .build();
        UserDto savedUser = userService.createUser(userCreationRequest);

        AssignRoleDto roleDetails = roleService.assignRole(savedUser.getId(), savedTenant.getId(), Constants.TENANT_ADMIN);


        return RegistrationResponse.builder()
                                   .tenantSlug(slug)
                                   .build();


    }
}
