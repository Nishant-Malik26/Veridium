package com.veridium.auth_service.exception.user;

import static com.veridium.auth_service.constants.ErrorMessages.USER_NOT_PART_OF_TENANT;

public class UserNotPartOfTenantException extends RuntimeException {
    public UserNotPartOfTenantException() {
        super(USER_NOT_PART_OF_TENANT);
    }
}
