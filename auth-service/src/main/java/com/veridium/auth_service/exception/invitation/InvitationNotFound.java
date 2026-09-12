package com.veridium.auth_service.exception.invitation;

import com.veridium.auth_service.constants.ErrorMessages;

public class InvitationNotFound extends RuntimeException {
    public InvitationNotFound() {
        super(ErrorMessages.INVITATION_NOT_FOUND);
    }
}
