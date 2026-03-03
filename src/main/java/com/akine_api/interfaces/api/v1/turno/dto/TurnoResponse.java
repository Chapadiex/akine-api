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
        String pacienteNombre,
        String pacienteApellido,
        String pacienteDni,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        int duracionMinutos,
        String estado,
        String tipoConsulta,
        String motivoConsulta,
        String notas,
        String telefonoContacto,
        UUID creadoPorUserId,
        String motivoCancelacion,
        Instant createdAt,
        Instant updatedAt
) {}
