package com.veridium.auth_service.exception.role;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String roleName) {
        super("Role not found with name : " + roleName);
    }

    public RoleNotFoundException() {
        super("Role not found");
    }
}
