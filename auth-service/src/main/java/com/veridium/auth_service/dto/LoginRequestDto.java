package com.veridium.auth_service.dto;

public record LoginRequestDto(String email, String password, String tenantSlug) {
}
