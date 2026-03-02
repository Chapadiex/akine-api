package com.akine_api.application.dto.result;

import com.akine_api.domain.model.TurnoEstado;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record TurnoResult(
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
        TurnoEstado estado,
        String motivoConsulta,
        String notas,
        Instant createdAt,
        Instant updatedAt
) {}
