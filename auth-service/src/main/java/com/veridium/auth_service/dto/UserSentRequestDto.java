package com.veridium.auth_service.dto;

import java.util.UUID;

public record UserSentRequestDto(String token, UUID tenantId, String tenantName, String receiverEmail) {
}
