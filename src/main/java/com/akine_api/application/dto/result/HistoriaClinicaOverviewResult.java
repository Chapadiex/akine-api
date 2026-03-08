package com.akine_api.application.dto.result;

import java.util.List;

public record HistoriaClinicaOverviewResult(
        HistoriaClinicaPacienteResult paciente,
        HistoriaClinicaLegajoStatusResult legajo,
        List<String> alertasClinicas,
        List<HistoriaClinicaAntecedenteResult> antecedentesRelevantes,
        List<HistoriaClinicaActiveCaseSummaryResult> casosActivos,
        HistoriaClinicaSesionSummaryResult ultimaSesion,
        List<AdjuntoClinicoResult> adjuntosRecientes,
        String profesionalHabitual,
        AtencionInicialSummaryResult atencionInicial,
        AtencionInicialEvaluacionResult evaluacionInicial,
        PlanTerapeuticoSummaryResult planTerapeuticoActivo
) {}
