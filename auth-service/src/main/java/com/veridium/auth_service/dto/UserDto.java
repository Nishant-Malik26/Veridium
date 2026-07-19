package com.veridium.auth_service.dto;

import java.util.UUID;


public record UserDto(UUID id, String email, String last_name, String first_name) {
}
