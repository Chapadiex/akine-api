package com.akine_api.interfaces.api.v1.profesional.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProfesionalEstadoRequest(
        boolean activo,
        LocalDate fechaDeBaja,
        @Size(max = 255)
        String motivoDeBaja
) {}
