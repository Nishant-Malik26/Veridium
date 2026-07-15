package com.veridium.auth_service.exception.tenant;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException() {
        super("Tenant not found");
    }
}
