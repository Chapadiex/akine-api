package com.akine_api.application.dto.result;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record HistoriaClinicaPacienteResult(
        UUID id,
        UUID consultorioId,
        String dni,
        String nombre,
        String apellido,
        String telefono,
        String email,
        LocalDate fechaNacimiento,
        String obraSocialNombre,
        String obraSocialPlan,
        String obraSocialNroAfiliado,
        boolean activo,
        int diagnosticosActivos,
        LocalDateTime ultimaSesionFecha,
        Instant updatedAt
) {}
