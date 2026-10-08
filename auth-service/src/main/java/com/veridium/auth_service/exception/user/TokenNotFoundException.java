package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class TokenNotFoundException extends RuntimeException {
    public TokenNotFoundException() {
        super(ErrorMessages.PASSWORD_RESET_TOKEN_NOT_FOUND);
    }
}
