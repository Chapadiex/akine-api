package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DiagnosticoClinicoEstadoRequest(
        @NotNull LocalDate fechaFin
) {}
