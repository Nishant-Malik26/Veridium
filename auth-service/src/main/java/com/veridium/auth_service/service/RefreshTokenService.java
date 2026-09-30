package com.veridium.auth_service.service;

import com.veridium.auth_service.dto.RefreshTokenDto;
import com.veridium.auth_service.dto.TenantDto;
import com.veridium.auth_service.dto.UserDto;
import com.veridium.auth_service.entity.RefreshToken;
import com.veridium.auth_service.entity.Tenant;
import com.veridium.auth_service.entity.User;
import com.veridium.auth_service.exception.tenant.TenantNotFoundException;
import com.veridium.auth_service.exception.user.TokenNotFoundException;
import com.veridium.auth_service.exception.user.UserNotFoundException;
import com.veridium.auth_service.repository.RefreshTokenRepository;
import com.veridium.auth_service.repository.TenantRepository;
import com.veridium.auth_service.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private  final JwtService jwtService;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public RefreshTokenDto getRefreshToken(String refreshTokenDto, String tenantSlug){
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refreshTokenDto).orElseThrow(TokenNotFoundException::new);
        if(refreshToken.isRevoked()) {
            throw new TokenNotFoundException();
            //TODO better error handling
        }
        User user = userRepository.findById(refreshToken.getUserId()).orElseThrow(UserNotFoundException::new);
        //TODO change the expection or something like it
        UserDto userDto = new UserDto(user.getId(), user.getEmail(), user.getFirst_name(),  user.getLast_name());
        refreshToken.setRevoked(true);
        Tenant tenant = tenantRepository.findBySlug(tenantSlug).orElseThrow(TenantNotFoundException::new);
        TenantDto tenantDto = new TenantDto(tenant.getId(), tenant.getSlug(), tenant.getStatus());
        String accessToken = jwtService.createJwtWithClaims(userDto, tenantDto);
        String newRefreshToken = String.valueOf(UUID.randomUUID());
        //TODO set acces token in cookies
        // also think about setting tenant slug in cookies??
        // set refresh token in cookies
        return new RefreshTokenDto(newRefreshToken);
    }
}
