package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class PasswordNewPasswordNotMatch extends RuntimeException {
    public PasswordNewPasswordNotMatch( ) {
        super(ErrorMessages.PASSWORD_AND_CONFIRM_PASSWORD_NOT_MATCH);
    }
}
