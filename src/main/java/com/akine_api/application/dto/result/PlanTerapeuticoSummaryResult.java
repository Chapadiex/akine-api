package com.akine_api.application.dto.result;

import com.akine_api.domain.model.PlanTerapeuticoEstado;

import java.util.List;
import java.util.UUID;

public record PlanTerapeuticoSummaryResult(
        UUID id,
        UUID atencionInicialId,
        UUID profesionalId,
        String profesionalNombre,
        PlanTerapeuticoEstado estado,
        String observacionesGenerales,
        List<PlanTratamientoDetalleResult> tratamientos
) {
}
