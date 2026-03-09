package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.UUID;

public record DiagnosticoClinicoRequest(
        @NotNull UUID profesionalId,
        UUID sesionId,
        @NotBlank @Size(max = 100) String diagnosticoCodigo,
        @NotNull LocalDate fechaInicio,
        @Size(max = 1000) String notas
) {}
