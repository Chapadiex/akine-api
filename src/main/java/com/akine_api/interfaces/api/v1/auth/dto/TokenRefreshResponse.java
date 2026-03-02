package com.akine_api.interfaces.api.v1.auth.dto;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn
) {}
