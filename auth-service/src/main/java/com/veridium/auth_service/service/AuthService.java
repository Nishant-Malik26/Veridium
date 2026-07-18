package com.veridium.auth_service.service;

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
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                                  .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword_hash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEnabled()) {
            throw new UserAccountDisabled();
        }

        Tenant tenant = tenantRepository.findBySlug(loginRequestDto.getTenantSlug())
                                        .orElseThrow(TenantNotFoundException::new);

        UserRole userRole = userRoleRepository.findByUserIdAndTenantId(user.getId(), tenant.getId())
                                              .orElseThrow(UserNotPartOfTenantException::new);
        UserDto userDto = UserDto.builder()
                                 .id(user.getId())
                                 .email(user.getEmail())
                                 .first_name(user.getFirst_name())
                                 .last_name(user.getLast_name())
                                 .build();
        TenantDto tenantDto = TenantDto.builder()
                                       .id(tenant.getId())
                                       .slug(tenant.getSlug())
                                       .status(tenant.getStatus())
                                       .build();

        RoleDto roleDto = RoleDto.builder()
                                 .id(userRole.getRole()
                                             .getId())
                                 .name(userRole.getRole()
                                               .getName())
                                 .description(userRole.getRole()
                                                      .getDescription())
                                 .build();
        String accessToken = jwtService.createJwtWithClaims(userDto, tenantDto);
        return LoginResponseDto.builder()
                               .accessToken(accessToken)
                               .userDto(userDto)
                               .tenantDto(tenantDto)
                               .roleDto(roleDto)
                               .build();

    }
}
