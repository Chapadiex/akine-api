package com.akine_api.interfaces.api.v1.colaborador.dto;

import java.time.Instant;
import java.util.UUID;

public record CargoEmpleadoCatalogoResponse(
        UUID id,
        String nombre,
        String slug,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
