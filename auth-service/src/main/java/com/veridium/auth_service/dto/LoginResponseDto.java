package com.veridium.auth_service.dto;


import java.util.List;

public record LoginResponseDto(String accessToken, String refreshToken, String message, UserDto userDto,
                               TenantDto tenantDto, List<RoleDto> roleDto) {
}
