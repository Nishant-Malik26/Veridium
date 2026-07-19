package com.veridium.auth_service.dto;

import java.util.UUID;


public record RoleDto(UUID id, String name, String description) {
}
