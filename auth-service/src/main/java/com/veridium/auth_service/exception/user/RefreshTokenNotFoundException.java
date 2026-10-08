package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class RefreshTokenNotFoundException extends RuntimeException {
    public RefreshTokenNotFoundException() {
        super(ErrorMessages.REFRESH_TOKEN_NOT_FOUND);
    }
}
