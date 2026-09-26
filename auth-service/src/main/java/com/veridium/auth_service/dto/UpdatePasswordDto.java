package com.veridium.auth_service.dto;

public record UpdatePasswordDto(String oldPassword, String newPassword, String confirmNewPassword) {
}
