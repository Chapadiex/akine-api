package com.akine_api.interfaces.api.v1.casoatencion.dto;

import com.akine_api.domain.model.CasoAtencionEstado;
import jakarta.validation.constraints.NotNull;

public record CambiarEstadoCasoAtencionRequest(
        @NotNull(message = "El nuevo estado es obligatorio")
        CasoAtencionEstado nuevoEstado
) {}
