package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class UnauthenticatedUserException extends RuntimeException {
    public UnauthenticatedUserException() {
        super(ErrorMessages.USER_UNAUTHENTICATED);
    }
}
