package com.akine_api.application.dto.result;

import com.akine_api.application.dto.command.SesionEvaluacionDTO;
import com.akine_api.application.dto.command.SesionExamenFisicoDTO;
import com.akine_api.application.dto.command.SesionIntervencionDTO;
import com.akine_api.domain.model.HistoriaClinicaOrigenRegistro;
import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SesionClinicaResult(
        UUID id,
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalId,
        UUID turnoId,
        UUID boxId,
        LocalDateTime fechaAtencion,
        HistoriaClinicaSesionEstado estado,
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
        HistoriaClinicaOrigenRegistro origenRegistro,
        UUID createdByUserId,
        UUID updatedByUserId,
        UUID closedByUserId,
        Instant createdAt,
        Instant updatedAt,
        Instant closedAt,
        List<AdjuntoClinicoResult> adjuntos
) {}
