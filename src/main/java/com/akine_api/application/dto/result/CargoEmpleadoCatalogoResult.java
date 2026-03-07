package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record CargoEmpleadoCatalogoResult(
        UUID id,
        String nombre,
        String slug,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
