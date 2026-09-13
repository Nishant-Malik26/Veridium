package com.veridium.auth_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.veridium.auth_service.constants.ErrorMessages;
import com.veridium.auth_service.constants.SuccessMessages;
import com.veridium.auth_service.dto.*;
import com.veridium.auth_service.exception.user.InvalidCredentialsException;
import com.veridium.auth_service.service.AuthService;
import com.veridium.auth_service.service.ForgotPasswordService;
import com.veridium.auth_service.service.InvitationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
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
    private final InvitationService invitationService;
    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
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

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/send-invitation")
    public ResponseEntity<?> sendInvitation(@RequestBody UserInvitationSendRequest userInvitationSendRequest) {
        try {
            if(invitationService.sendInvitation(userInvitationSendRequest)){
                return ResponseEntity.ok().build();
            }
        }
        catch (JsonProcessingException _){

        }
        return ResponseEntity.badRequest()
                             .build();
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<ApiResponse<?>> acceptInvitation(@Valid @RequestBody UserAcceptionAndCreationDto userAcceptionAndCreationDto) {
        LoginRequestDto loginRequestDto = new LoginRequestDto(
                userAcceptionAndCreationDto.userCreationRequest().email(),
                userAcceptionAndCreationDto.userCreationRequest().password(),
                userAcceptionAndCreationDto.userCreationRequest().tenantSlug());

        if(invitationService.acceptInvitation(userAcceptionAndCreationDto)){
            LoginResponseDto responseDto = authService.login(loginRequestDto);
            ApiResponse<LoginResponseDto> response = ApiResponse.<LoginResponseDto>builder()
                                                                .success(true)
                                                                .message(SuccessMessages.USER_LOGGED_IN_SUCCESSFULLY)
                                                                .data(responseDto)
                                                                .build();
            return ResponseEntity.status(HttpStatus.OK)
                                 .body(response);
        }
        else {
            return ResponseEntity.badRequest()
                                 .body(new ApiResponse<>(false, ErrorMessages.INVITATION_NOT_FOUND, null));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<?>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        String refreshToken = "";
        try{
             refreshToken = forgotPasswordService.forgotPassword(forgotPasswordDto);
        }
        catch  (Exception _){  }

        ApiResponse<RefreshTokenDto> response = ApiResponse.<RefreshTokenDto>builder()
                                                           .success(true)
                                                           .message(SuccessMessages.FORGOT_PASSWORD_SUCCESSFULLY)
                                                           .data(new RefreshTokenDto(refreshToken)).build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
