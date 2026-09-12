package com.veridium.auth_service.dto;

public record ForgotPasswordEmailEvent(String email, String firstName, String lastName, String otp,String tenantName) {
}
