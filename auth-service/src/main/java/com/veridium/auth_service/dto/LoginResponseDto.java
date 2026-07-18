package com.veridium.auth_service.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LoginResponseDto {
    String accessToken;
    String refreshToken;
    String message;
    UserDto userDto;
    TenantDto tenantDto;
    RoleDto roleDto;
}
