package com.veridium.auth_service.dto;


public record UserCreationRequest(String tenantSlug, String first_name, String last_name, String email,
                                  String password) {
}
