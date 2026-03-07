package com.akine_api.application.dto.command;

import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateSesionClinicaCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID sesionId,
        UUID profesionalId,
        UUID turnoId,
        UUID boxId,
        LocalDateTime fechaAtencion,
        HistoriaClinicaTipoAtencion tipoAtencion,
        String motivoConsulta,
        String resumenClinico,
        String subjetivo,
        String objetivo,
        String evaluacion,
        String plan,
        UUID actorUserId
) {}
