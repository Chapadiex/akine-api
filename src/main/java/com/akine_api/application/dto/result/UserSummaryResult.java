package com.akine_api.application.dto.result;

import java.util.List;
import java.util.UUID;

public record UserSummaryResult(
        UUID id,
        String email,
        String firstName,
        String lastName,
        String status,
        List<String> roles
) {}
