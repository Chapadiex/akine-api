package com.akine_api.interfaces.api.v1.profesional.dto;

import java.time.Instant;
import java.util.UUID;

public record ProfesionalResponse(
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
