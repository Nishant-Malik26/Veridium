package com.veridium.auth_service.service;

import com.veridium.auth_service.constants.SuccessMessages;
import com.veridium.auth_service.dto.*;
import com.veridium.auth_service.entity.RefreshToken;
import com.veridium.auth_service.entity.Tenant;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.entity.UserRole;
import com.veridium.auth_service.exception.user.InvalidCredentialsException;
import com.veridium.auth_service.exception.user.UserAccountDisabled;
import com.veridium.auth_service.exception.user.UserNotPartOfTenantException;
import com.veridium.auth_service.repository.RefreshTokenRepository;
import com.veridium.auth_service.repository.TenantRepository;
import com.veridium.auth_service.repository.UserRepository;
import com.veridium.auth_service.repository.UserRoleRepository;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.xml.crypto.dsig.DigestMethod;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TenantRepository tenantRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        List<UserRole> userRole = userRoleRepository.findByUserEmailAndTenantSlug(loginRequestDto.email(), loginRequestDto.tenantSlug());
        if(userRole.isEmpty()) {
            throw new UserNotPartOfTenantException();
        }
        User user = userRole.getFirst().getUser();
        Tenant tenant = userRole.getFirst().getTenant();

        if (!passwordEncoder.matches(loginRequestDto.password(), user.getPassword_hash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEnabled()) {
            throw new UserAccountDisabled();
        }

        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getFirst_name(), user.getLast_name());
        TenantDto tenantDto = new TenantDto(tenant.getId(), tenant.getSlug(), tenant.getStatus());
        List<RoleDto> roles = userRole.stream().map(
                ur -> new RoleDto(
                        ur.getRole().getId(),
                        ur.getRole()
                                .getName(), ur.getRole()
                                                    .getDescription()
                )
        ).toList();
        String refreshToken = UUID.randomUUID().toString();
        RefreshToken refreshTokenDto = new RefreshToken(refreshToken,userDto.id(),"mobile", "192.168.30.12",false);
        refreshTokenRepository.save(refreshTokenDto);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setDomain("localhost");
        cookie.setPath("/");

        String accessToken = jwtService.createJwtWithClaims(userDto, tenantDto);
        return new LoginResponseDto(accessToken, refreshToken, SuccessMessages.USER_LOGGED_IN_SUCCESSFULLY, userDto, tenantDto, roles);


    }
}
