package com.veridium.auth_service.dto;

import com.veridium.auth_service.utils.TenantStatus;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

@Value
@Builder
@Jacksonized
public class TenantDto {
    UUID id;
    String slug;
    TenantStatus status;
}
