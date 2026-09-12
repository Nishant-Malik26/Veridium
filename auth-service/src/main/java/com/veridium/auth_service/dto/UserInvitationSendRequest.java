package com.veridium.auth_service.dto;

import java.util.UUID;

public record UserInvitationSendRequest(UUID tenantId, String tenantName, String receiverEmail) {}
