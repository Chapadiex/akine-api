package com.akine_api.application.dto.result;

import com.akine_api.domain.model.TipoConsulta;
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
        String pacienteNombre,
        String pacienteApellido,
        String pacienteDni,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        int duracionMinutos,
        TurnoEstado estado,
        TipoConsulta tipoConsulta,
        String motivoConsulta,
        String notas,
        String telefonoContacto,
        UUID creadoPorUserId,
        String motivoCancelacion,
        Instant createdAt,
        Instant updatedAt
) {}
