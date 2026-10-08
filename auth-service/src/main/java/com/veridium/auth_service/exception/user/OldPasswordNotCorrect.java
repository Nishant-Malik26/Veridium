package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class OldPasswordNotCorrect extends RuntimeException {
    public OldPasswordNotCorrect() {
        super(ErrorMessages.ENTERED_OLD_PASSWORD_WRONG);
    }
}
