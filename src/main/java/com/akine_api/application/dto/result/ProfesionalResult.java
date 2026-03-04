package com.akine_api.application.dto.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProfesionalResult(
        UUID id,
        UUID consultorioId,
        String nombre,
        String apellido,
        String nroDocumento,
        String matricula,
        String especialidad,
        String especialidades,
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
