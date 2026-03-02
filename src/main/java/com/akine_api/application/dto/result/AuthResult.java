package com.akine_api.application.dto.result;

import java.util.List;
import java.util.UUID;

public record AuthResult(
        String accessToken,
        String refreshToken,
        long expiresInMs,
        UUID userId,
        String email,
        String firstName,
        String lastName,
        List<String> roles,
        List<UUID> consultorioIds
) {}
