package com.akine_api.interfaces.api.v1.asignacion.dto;

import java.util.UUID;

public record AsignacionResponse(
        UUID id,
        UUID profesionalId,
        UUID consultorioId,
        String profesionalNombre,
        String profesionalApellido,
        boolean activo
) {}
