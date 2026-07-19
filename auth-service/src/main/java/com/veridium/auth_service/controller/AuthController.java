package com.veridium.auth_service.controller;

import com.veridium.auth_service.constants.ErrorMessages;
import com.veridium.auth_service.constants.SuccessMessages;
import com.veridium.auth_service.dto.ApiResponse;
import com.veridium.auth_service.dto.LoginRequestDto;
import com.veridium.auth_service.dto.LoginResponseDto;
import com.veridium.auth_service.exception.user.InvalidCredentialsException;
import com.veridium.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            LoginResponseDto responseDto = authService.login(loginRequestDto);
            ApiResponse<LoginResponseDto> response = ApiResponse.<LoginResponseDto>builder()
                                                                .success(true)
                                                                .message(SuccessMessages.USER_LOGGED_IN_SUCCESSFULLY)
                                                                .data(responseDto)
                                                                .build();
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(response);
        } catch (BadCredentialsException | InvalidCredentialsException ex) {
            return ResponseEntity.badRequest()
                                 .body(new ApiResponse<>(false, ErrorMessages.EMAIL_OR_PASSWORD_WRONG, null));

        }
    }
}
