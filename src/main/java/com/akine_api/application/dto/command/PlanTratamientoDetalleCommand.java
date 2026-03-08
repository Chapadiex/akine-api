package com.akine_api.application.dto.command;

import com.akine_api.domain.model.PlanTratamientoCaracter;

import java.time.LocalDate;

public record PlanTratamientoDetalleCommand(
        String tratamientoId,
        int cantidadSesiones,
        String frecuenciaSugerida,
        PlanTratamientoCaracter caracterCaso,
        LocalDate fechaEstimadaInicio,
        boolean requiereAutorizacion,
        String observaciones,
        String observacionesAdministrativas
) {
}
