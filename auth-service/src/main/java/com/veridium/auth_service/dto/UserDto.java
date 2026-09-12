package com.veridium.auth_service.dto;

import java.util.UUID;


public record UserDto(UUID id, String email,  String first_name, String last_name) {
}
