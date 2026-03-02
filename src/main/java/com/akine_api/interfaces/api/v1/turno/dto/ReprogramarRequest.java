package com.akine_api.interfaces.api.v1.turno.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReprogramarRequest(
        @NotNull(message = "La nueva fecha y hora es obligatoria")
        LocalDateTime nuevaFechaHoraInicio
) {}
