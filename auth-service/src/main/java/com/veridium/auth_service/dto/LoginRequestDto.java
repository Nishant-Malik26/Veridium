package com.veridium.auth_service.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class LoginRequestDto {
    String email;
    String password;
    String tenantSlug;
}
