package com.veridium.auth_service.constants;

public class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String USER_ALREADY_HAS_ROLE = "User already has this role in this tenant.";

    public static final String USER_NOT_FOUND = "User not found.";

    public static final String TENANT_NOT_FOUND = "Tenant not found.";

    public static final String ROLE_NOT_FOUND = "Role not found.";
}
