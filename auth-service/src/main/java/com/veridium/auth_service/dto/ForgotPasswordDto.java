package com.veridium.auth_service.dto;

public record ForgotPasswordDto(String email, String tenantSlug, String device, String ip) {
}
