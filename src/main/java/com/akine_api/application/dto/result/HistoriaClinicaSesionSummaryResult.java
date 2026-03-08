package com.akine_api.application.dto.result;

import com.akine_api.domain.model.HistoriaClinicaSesionEstado;
import com.akine_api.domain.model.HistoriaClinicaTipoAtencion;

import java.time.LocalDateTime;
import java.util.UUID;

public record HistoriaClinicaSesionSummaryResult(
        UUID sesionId,
        UUID profesionalId,
        String profesionalNombre,
        LocalDateTime fechaAtencion,
        HistoriaClinicaSesionEstado estado,
        HistoriaClinicaTipoAtencion tipoAtencion,
        String resumen
) {}
