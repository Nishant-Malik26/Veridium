package com.veridium.auth_service.dto;

import java.util.UUID;

public record UserInvitationAcceptRequest(String token, UUID tenantId) {
}
