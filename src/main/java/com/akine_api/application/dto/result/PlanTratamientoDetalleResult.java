package com.akine_api.application.dto.result;

import com.akine_api.domain.model.PlanTratamientoCaracter;

import java.time.LocalDate;
import java.util.UUID;

public record PlanTratamientoDetalleResult(
        UUID id,
        String tratamientoId,
        String tratamientoNombre,
        String tratamientoCategoriaCodigo,
        String tratamientoCategoriaNombre,
        String tratamientoTipo,
        boolean tratamientoRequiereAutorizacion,
        boolean tratamientoRequierePrescripcionMedica,
        Integer tratamientoDuracionSugeridaMinutos,
        int cantidadSesiones,
        String frecuenciaSugerida,
        PlanTratamientoCaracter caracterCaso,
        LocalDate fechaEstimadaInicio,
        boolean requiereAutorizacion,
        String observaciones,
        String observacionesAdministrativas
) {
}
