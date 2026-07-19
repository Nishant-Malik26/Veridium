package com.veridium.auth_service.dto;

import java.time.Instant;

public record ErrorResponse(String message, int status, Instant timestamp) {
}
