package com.veridium.auth_service.dto;

import com.veridium.auth_service.utils.AccessTokenType;

public record AccessToken(String accessToken, AccessTokenType accessTokenType) {
}
