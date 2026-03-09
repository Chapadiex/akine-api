package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SesionClinicaRequest(
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
        SesionEvaluacionRequest evaluacionEstructurada,
        SesionExamenFisicoRequest examenFisico,
        List<SesionIntervencionRequest> intervenciones
) {}
