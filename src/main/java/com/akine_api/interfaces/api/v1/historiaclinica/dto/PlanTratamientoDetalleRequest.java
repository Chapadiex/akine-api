package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import com.akine_api.domain.model.PlanTratamientoCaracter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PlanTratamientoDetalleRequest(
        @NotBlank @Size(max = 100) String tratamientoId,
        @Min(1) int cantidadSesiones,
        @Size(max = 120) String frecuenciaSugerida,
        @NotNull PlanTratamientoCaracter caracterCaso,
        LocalDate fechaEstimadaInicio,
        boolean requiereAutorizacion,
        @Size(max = 1000) String observaciones,
        @Size(max = 1000) String observacionesAdministrativas
) {
}
