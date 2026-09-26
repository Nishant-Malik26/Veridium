package com.veridium.auth_service.dto;

public record ResetPasswordDto(String token, String newPassword, String confirmPassword) {
}
