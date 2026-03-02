package com.akine_api.application.dto.result;

public record TokenPairResult(
        String accessToken,
        String refreshToken,
        long expiresInMs
) {}
