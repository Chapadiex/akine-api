package com.akine_api.interfaces.api.v1.colaborador.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ColaboradorEstadoRequest(
        @NotNull Boolean activo,
        LocalDate fechaDeBaja,
        String motivoDeBaja
) {}
