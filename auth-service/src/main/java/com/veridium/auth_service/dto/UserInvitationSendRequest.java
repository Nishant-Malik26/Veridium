package com.veridium.auth_service.dto;

import com.veridium.auth_service.entity.Role;

import java.util.List;
import java.util.UUID;

public record UserInvitationSendRequest(UUID tenantId, String tenantName, String receiverEmail, List<UUID> roles) {}
