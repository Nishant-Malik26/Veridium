package com.veridium.auth_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.veridium.auth_service.constants.ErrorMessages;
import com.veridium.auth_service.constants.SuccessMessages;
import com.veridium.auth_service.dto.*;
import com.veridium.auth_service.security.TenantAuthenticationToken;
import com.veridium.auth_service.service.*;
import com.veridium.auth_service.utils.AccessTokenType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.*;

import static com.veridium.auth_service.constants.Constants.REFRESH_TOKEN_COOKIE;
import static com.veridium.auth_service.constants.SuccessMessages.TOKEN_REFRESHED_SUCCESSFULLY;
import static org.springframework.http.HttpHeaders.SET_COOKIE;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final InvitationService invitationService;
    private final ForgotPasswordService forgotPasswordService;
    private final ResetPasswordService resetPasswordService;
    private final UpdatePasswordService updatePasswordService;
    private  final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
            LoginResponseDto responseDto = authService.login(loginRequestDto);
            ApiResponse<LoginResponseDto> response = ApiResponse.<LoginResponseDto>builder()
                                                                .success(true)
                                                                .message(SuccessMessages.USER_LOGGED_IN_SUCCESSFULLY)
                                                                .data(responseDto)
                                                                .build();
            HttpHeaders headers = new HttpHeaders();
            headers.add(SET_COOKIE,responseDto.refreshToken());
            return ResponseEntity.status(HttpStatus.OK).headers(headers)
                                 .body(response);
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

        forgotPasswordService.forgotPassword(forgotPasswordDto);


        ApiResponse<RefreshTokenDto> response = ApiResponse.<RefreshTokenDto>builder()
                                                           .success(true)
                                                           .message(SuccessMessages.FORGOT_PASSWORD_SUCCESSFULLY)
                                                           .build();
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<?>> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto){
        RefreshTokenDto refreshTokenDto = resetPasswordService.resetPassword(resetPasswordDto);
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, refreshTokenDto.refreshToken())
                                                          .httpOnly(true)
                                                          //.secure(true)
                                                          .path("/api/auth/get-refresh-token")
                                                          .maxAge(Duration.ofDays(7))
                                                          .sameSite("Lax")
                                                          .build();
        return ResponseEntity.ok().header(SET_COOKIE, refreshTokenCookie.toString()).build();
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto, @AuthenticationPrincipal TenantAuthenticationToken authenticatedUser){
        updatePasswordService.updatePassword(updatePasswordDto, authenticatedUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/get-refresh-token")
    public ResponseEntity<ApiResponse<AccessToken>> getAccessToken(@CookieValue("refreshToken") String refreshTokenDto, @AuthenticationPrincipal TenantAuthenticationToken authenticatedUser){
        //TODO : As user may be unauthenticated here so tenant slug should come from header or something needs to be checked
        var accessRefreshToken =  refreshTokenService.getRefreshToken(refreshTokenDto,authenticatedUser.getTenantSlug());
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE, accessRefreshToken.refreshToken())
                                                      .httpOnly(true)
                                                      .path("/api/auth/get-refresh-token")
                                                      .maxAge(Duration.ofDays(7))
                                                      .sameSite("Lax")
                                                      .build();
        return ResponseEntity.status(HttpStatus.OK)
                             .header(SET_COOKIE, responseCookie.toString())
                             .body(ApiResponse.<AccessToken>builder()
                                              .message(TOKEN_REFRESHED_SUCCESSFULLY)
                                              .data(new AccessToken(accessRefreshToken.accessToken(), AccessTokenType.BEARER))
                                              .build());
    }

}
