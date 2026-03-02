package com.akine_api.interfaces.api.v1.turno.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record TurnoResponse(
        UUID id,
        UUID consultorioId,
        UUID profesionalId,
        String profesionalNombre,
        String profesionalApellido,
        UUID boxId,
        String boxNombre,
        UUID pacienteId,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        int duracionMinutos,
        String estado,
        String motivoConsulta,
        String notas,
        Instant createdAt,
        Instant updatedAt
) {}
