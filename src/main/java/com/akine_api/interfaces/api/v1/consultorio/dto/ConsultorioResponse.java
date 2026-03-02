package com.akine_api.interfaces.api.v1.consultorio.dto;

import java.time.Instant;
import java.util.UUID;

public record ConsultorioResponse(
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
