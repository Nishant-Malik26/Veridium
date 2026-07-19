package com.veridium.auth_service.controller;

import com.veridium.auth_service.constants.SuccessMessages;
import com.veridium.auth_service.dto.ApiResponse;
import com.veridium.auth_service.dto.RegistrationRequest;
import com.veridium.auth_service.dto.RegistrationResponse;
import com.veridium.auth_service.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/tenant")
public class TenantController {
    private final TenantService tenantService;

    public TenantController(TenantService service) {
        this.tenantService = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegistrationResponse>> createTenant(@RequestBody RegistrationRequest registrationRequest) {
        RegistrationResponse res = tenantService.createTenant(registrationRequest);
        ApiResponse<RegistrationResponse> response = ApiResponse.<RegistrationResponse>builder()
                                                                .success(true)
                                                                .message(SuccessMessages.TENANT_CREATE_SUCCESSFULLY)
                                                                .data(res)
                                                                .build();
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(response);

    }
}
