package com.veridium.auth_service.dto;

import com.veridium.auth_service.utils.TenantStatus;

import java.util.UUID;


public record TenantDto(UUID id, String slug, TenantStatus status) {
}
