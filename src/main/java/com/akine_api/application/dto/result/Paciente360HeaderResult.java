package com.akine_api.application.dto.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Paciente360HeaderResult(
        UUID id,
        UUID consultorioId,
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
        boolean activo,
        boolean coberturaVigente,
        String coberturaResumen,
        Instant createdAt,
        Instant updatedAt
) {}
