package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException() {
        super(ErrorMessages.REFRESH_TOKEN_EXPIRED);
    }
}
