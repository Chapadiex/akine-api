package com.akine_api.interfaces.api.v1.especialidad.dto;

import java.time.Instant;
import java.util.UUID;

public record EspecialidadResponse(
        UUID id,
        UUID consultorioId,
        String nombre,
        String slug,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
