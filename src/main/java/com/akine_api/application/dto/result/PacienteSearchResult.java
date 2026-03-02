package com.akine_api.application.dto.result;

import java.util.UUID;

public record PacienteSearchResult(
        UUID id,
        String dni,
        String nombre,
        String apellido,
        String telefono,
        String email,
        boolean activo,
        boolean linkedToConsultorio
) {}
