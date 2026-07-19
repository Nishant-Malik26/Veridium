package com.veridium.auth_service.service;

import com.veridium.auth_service.constants.SuccessMessages;
import com.veridium.auth_service.dto.*;
import com.veridium.auth_service.entity.Tenant;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.exception.tenant.TenantNotFoundException;
import com.veridium.auth_service.exception.user.InvalidCredentialsException;
import com.veridium.auth_service.exception.user.UserAccountDisabled;
import com.veridium.auth_service.exception.user.UserNotPartOfTenantException;
import com.veridium.auth_service.repository.TenantRepository;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TenantRepository tenantRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.email())
                                  .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword_hash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEnabled()) {
            throw new UserAccountDisabled();
        }

        Tenant tenant = tenantRepository.findBySlug(loginRequestDto.tenantSlug())
                                        .orElseThrow(TenantNotFoundException::new);

        UserRole userRole = userRoleRepository.findByUserIdAndTenantId(user.getId(), tenant.getId())
                                              .orElseThrow(UserNotPartOfTenantException::new);
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getFirst_name(), user.getLast_name());
        TenantDto tenantDto = new TenantDto(tenant.getId(), tenant.getSlug(), tenant.getStatus());
        RoleDto roleDto = new RoleDto(userRole.getRole()
                                              .getId(), userRole.getRole()
                                                                .getName(), userRole.getRole()
                                                                                    .getDescription());

        String accessToken = jwtService.createJwtWithClaims(userDto, tenantDto);
        return new LoginResponseDto(accessToken, null, SuccessMessages.USER_LOGGED_IN_SUCCESSFULLY, userDto, tenantDto, roleDto);


    }
}
