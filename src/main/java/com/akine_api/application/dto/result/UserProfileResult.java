package com.akine_api.application.dto.result;

import java.util.List;
import java.util.UUID;

public record UserProfileResult(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String phone,
        String status,
        List<String> roles,
        List<UUID> consultorioIds
) {}
