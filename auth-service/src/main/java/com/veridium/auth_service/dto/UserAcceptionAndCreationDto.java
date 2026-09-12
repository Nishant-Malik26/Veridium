package com.veridium.auth_service.dto;

import jakarta.validation.constraints.NotNull;

public record UserAcceptionAndCreationDto (
    @NotNull
    UserCreationRequest userCreationRequest,
    @NotNull
    UserInvitationAcceptRequest userInvitationAcceptRequest
){}
