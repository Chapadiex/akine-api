package com.akine_api.interfaces.api.v1.paciente.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record PacienteResponse(
        UUID id,
        String dni,
        String nombre,
        String apellido,
        String telefono,
        String email,
        LocalDate fechaNacimiento,
        String sexo,
        String domicilio,
        String nacionalidad,
        String estadoCivil,
        List<String> profesiones,
        String obraSocialNombre,
        String obraSocialPlan,
        String obraSocialNroAfiliado,
        UUID userId,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
