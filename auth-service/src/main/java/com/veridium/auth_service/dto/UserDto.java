package com.veridium.auth_service.dto;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class UserDto {
    String last_name;
    String email;
    UUID id;
    String first_name;
    
}
