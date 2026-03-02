package com.akine_api.interfaces.api.v1.duracion.dto;

import jakarta.validation.constraints.NotNull;

public record DuracionRequest(
        @NotNull(message = "Los minutos son obligatorios")
        Integer minutos
) {}
