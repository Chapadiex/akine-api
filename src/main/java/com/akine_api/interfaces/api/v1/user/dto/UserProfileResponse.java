package com.akine_api.interfaces.api.v1.user.dto;

import java.util.List;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String status,
        List<String> roles,
        List<UUID> consultorioIds
) {}
