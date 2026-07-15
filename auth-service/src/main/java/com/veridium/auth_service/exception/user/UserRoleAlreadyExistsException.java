package com.veridium.auth_service.exception.user;

public class UserRoleAlreadyExistsException extends RuntimeException {
    public UserRoleAlreadyExistsException() {
        super("User already has this role in this tenant.");
    }
}
