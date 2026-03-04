package com.akine_api.interfaces.api.v1.profesional.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ProfesionalResponse(
        UUID id,
        UUID consultorioId,
        String nombre,
        String apellido,
        String nroDocumento,
        String matricula,
        String especialidad,
        List<String> especialidades,
        String email,
        String telefono,
        String domicilio,
        String fotoPerfilUrl,
        LocalDate fechaAlta,
        LocalDate fechaBaja,
        String motivoBaja,
        int consultoriosAsociados,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
