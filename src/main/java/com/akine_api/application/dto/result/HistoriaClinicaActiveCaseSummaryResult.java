package com.akine_api.application.dto.result;

import com.akine_api.domain.model.DiagnosticoClinicoEstado;

import java.time.LocalDate;
import java.util.UUID;

public record HistoriaClinicaActiveCaseSummaryResult(
        UUID diagnosticoId,
        UUID profesionalId,
        String profesionalNombre,
        String codigo,
        String descripcion,
        DiagnosticoClinicoEstado estado,
        LocalDate fechaInicio,
        int cantidadSesiones,
        String ultimaEvolucionResumen
) {}
