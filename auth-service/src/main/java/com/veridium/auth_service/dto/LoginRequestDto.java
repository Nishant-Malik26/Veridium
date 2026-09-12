package com.veridium.auth_service.dto;

import com.veridium.auth_service.constants.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = ErrorMessages.EMAIL_REQUIRED) @Email(message = ErrorMessages.EMAIL_FORMAT_NOT_CORRECT) String email,
        @NotBlank(message = ErrorMessages.EMAIL_REQUIRED) String password,
        @NotBlank(message = ErrorMessages.TENANT_SLUG_REQUIRED) String tenantSlug) {
}
