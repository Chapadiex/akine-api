package com.akine_api.interfaces.api.v1.obrasocial.dto;

import com.akine_api.domain.model.ObraSocialEstado;
import jakarta.validation.constraints.NotNull;

public record ChangeEstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        ObraSocialEstado estado
) {}

