package com.akine_api.application.dto.result;

import java.util.UUID;

public record ProfesionalConsultorioResult(
        UUID id,
        UUID profesionalId,
        UUID consultorioId,
        String profesionalNombre,
        String profesionalApellido,
        boolean activo
) {}
