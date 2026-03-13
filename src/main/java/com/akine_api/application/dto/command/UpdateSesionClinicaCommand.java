package com.akine_api.application.dto.command;

import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UpdateSesionClinicaCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalId,
        UUID sesionId,
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
        SesionEvaluacionDTO evaluacionEstructurada,
        SesionExamenFisicoDTO examenFisico,
        List<SesionIntervencionDTO> intervenciones,
        UUID actorUserId
) {}
