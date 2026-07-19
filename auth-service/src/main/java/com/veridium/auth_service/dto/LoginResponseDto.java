package com.veridium.auth_service.dto;


public record LoginResponseDto(String accessToken, String refreshToken, String message, UserDto userDto,
                               TenantDto tenantDto, RoleDto roleDto) {
}
