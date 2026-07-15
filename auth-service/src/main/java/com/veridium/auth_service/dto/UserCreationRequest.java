package com.veridium.auth_service.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UserCreationRequest {
    String tenantSlug;
    String first_name;
    String last_name;
    String email;
    String password;
}
