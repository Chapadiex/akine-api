package com.akine_api.interfaces.api.v1.paciente.dto;

import java.util.UUID;

public record PacienteSearchResponse(
        UUID id,
        String dni,
        String nombre,
        String apellido,
        String telefono,
        String email,
        boolean activo,
        boolean linkedToConsultorio
) {}
