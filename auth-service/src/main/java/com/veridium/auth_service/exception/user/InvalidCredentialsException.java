package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super(ErrorMessages.EMAIL_OR_PASSWORD_WRONG);
    }
}
