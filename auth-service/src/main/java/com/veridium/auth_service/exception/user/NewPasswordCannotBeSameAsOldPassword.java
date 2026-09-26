package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class NewPasswordCannotBeSameAsOldPassword extends RuntimeException {
    public NewPasswordCannotBeSameAsOldPassword() {
        super(ErrorMessages.NEW_PASSWORD_CANNOT_BE_SAME_AS_OLD_PASSWORD);
    }
}
