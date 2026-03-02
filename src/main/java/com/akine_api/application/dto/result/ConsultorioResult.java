package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record ConsultorioResult(
        UUID id,
        String name,
        String cuit,
        String address,
        String phone,
        String email,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
