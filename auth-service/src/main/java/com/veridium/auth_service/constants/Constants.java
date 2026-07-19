package com.veridium.auth_service.constants;

import java.util.Arrays;
import java.util.List;

public class Constants {
    private Constants() {
    }

    public static final String TENANT_ADMIN = "TENANT_ADMIN";
    public static final String SUCCESS = "success";
    public static final String STATUS = "status";
    public static final String ERROR = "error";
    public static final String MESSAGE = "message";
    public static final String PATH = "path";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String FORBIDDEN = "Forbidden";
    public static final List<String> PUBLIC_ENDPOINTS = Arrays.asList("/auth/logout", "/auth/login", "/auth/register", "/auth/tenant/register", "/actuator/health", "/swagger-ui", "/v3/api-docs");
}
