package com.veridium.auth_service.dto;

public record UserInvitationAcceptRequest(String token, String tenantId) {
}
