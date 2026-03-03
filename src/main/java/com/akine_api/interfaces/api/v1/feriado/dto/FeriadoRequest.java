package com.akine_api.interfaces.api.v1.feriado.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record FeriadoRequest(
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,
        @Size(max = 200, message = "La descripción no puede superar 200 caracteres")
        String descripcion
) {}
