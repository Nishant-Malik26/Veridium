package com.veridium.auth_service.exception.user;

import com.veridium.auth_service.constants.ErrorMessages;

public class UserAccountDisabled extends RuntimeException {
    public UserAccountDisabled() {
        super(ErrorMessages.USER_ACCOUNT_DISABLED);
    }
}
