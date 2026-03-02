package com.akine_api.interfaces.api.v1.turno.dto;

import com.akine_api.domain.model.TurnoEstado;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        TurnoEstado nuevoEstado
) {}
