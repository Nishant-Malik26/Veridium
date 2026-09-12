package com.veridium.auth_service.dto;

public record OutboxEventDto (long id, String aggregateType){}
