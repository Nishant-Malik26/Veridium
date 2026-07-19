package com.veridium.auth_service.constants;

public class ErrorMessages {
    private ErrorMessages() {
    }

    public static final String USER_ALREADY_HAS_ROLE = "User already has this role in this tenant.";

    public static final String USER_NOT_FOUND = "User not found.";
    public static final String USER_NOT_FOUND_WITH_USERNAME = "User not found with username: ";

    public static final String TENANT_NOT_FOUND = "Tenant not found.";

    public static final String ROLE_NOT_FOUND = "Role not found.";
    public static final String USER_NOT_LOGGED_IN = "Role not found.";
    public static final String USER_DONT_HAVE_PERMISSION = "You do not have permission to access this resource.";


    public static final String EMAIL_OR_PASSWORD_WRONG = "Either entered email or password is wrong.";
    public static final String USER_ACCOUNT_DISABLED = "This user is account disabled.Please contact administrator";

    public static final String USER_NOT_PART_OF_TENANT = "The user is not a part of this tenant.";


}
