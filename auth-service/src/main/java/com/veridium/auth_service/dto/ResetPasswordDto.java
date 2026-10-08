package com.veridium.auth_service.dto;

import java.util.UUID;

public record ResetPasswordDto(String token, UUID userId, String newPassword, String confirmPassword, String device,String ip) {
}
