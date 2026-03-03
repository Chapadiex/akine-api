package com.akine_api.interfaces.api.v1.turno.dto;

import com.akine_api.domain.model.TurnoEstado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambiarEstadoRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        TurnoEstado nuevoEstado,
        @Size(max = 500, message = "El motivo no puede superar 500 caracteres")
        String motivo
) {}
