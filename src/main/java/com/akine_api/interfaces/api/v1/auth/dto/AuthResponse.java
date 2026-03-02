package com.akine_api.interfaces.api.v1.auth.dto;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserInfo user
) {
    public record UserInfo(
            UUID id,
            String email,
            String firstName,
            String lastName,
            List<String> roles,
            List<UUID> consultorioIds
    ) {}
}
