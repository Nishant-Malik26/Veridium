package com.veridium.auth_service.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ValidationErrorResponseDto {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private Map<String, String> details; // Holds field/parameter names and their error messages

    public ValidationErrorResponseDto(int status, String error, Map<String, String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.details = details;
    }

    // Getters and Setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public Map<String, String> getDetails() { return details; }
}
