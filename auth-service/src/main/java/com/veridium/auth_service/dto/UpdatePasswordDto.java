package com.veridium.auth_service.dto;

import com.veridium.auth_service.constants.ValidationMessages;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordDto(@NotBlank(message = ValidationMessages.OLD_PASSWORD_NOT_BLANK) String oldPassword, @NotBlank(message = ValidationMessages.NEW_PASSWORD_NOT_BLANK) @Size(min=4, max= 32) String newPassword, @Size(min=4, max= 32) String confirmNewPassword) {
}
