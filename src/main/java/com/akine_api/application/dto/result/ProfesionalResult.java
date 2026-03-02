package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record ProfesionalResult(
        UUID id,
        UUID consultorioId,
        String nombre,
        String apellido,
        String matricula,
        String especialidad,
        String email,
        String telefono,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
