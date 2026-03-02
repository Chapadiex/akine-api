package com.akine_api.interfaces.api.v1.admin.dto;

import java.util.List;
import java.util.UUID;

public record UserSummaryResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String status,
        List<String> roles
) {}
