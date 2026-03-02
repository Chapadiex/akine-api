package com.akine_api.interfaces.api.v1.asignacion.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AsignacionRequest(
        @NotNull(message = "profesionalId es obligatorio")
        UUID profesionalId
) {}
