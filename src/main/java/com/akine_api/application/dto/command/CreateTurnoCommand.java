package com.akine_api.application.dto.command;

import com.akine_api.domain.model.TipoConsulta;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTurnoCommand(
        UUID consultorioId,
        UUID profesionalId,
        UUID boxId,
        UUID pacienteId,
        LocalDateTime fechaHoraInicio,
        int duracionMinutos,
        String motivoConsulta,
        String notas,
        TipoConsulta tipoConsulta,
        String telefonoContacto,
        UUID creadoPorUserId
) {}
