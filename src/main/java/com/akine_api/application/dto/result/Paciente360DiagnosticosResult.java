package com.akine_api.application.dto.result;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record Paciente360DiagnosticosResult(
        long totalActivos,
        LocalDate ultimaFechaRegistrada,
        List<Item> items,
        int page,
        int size,
        long total
) {
    public record Item(
            UUID id,
            String nombre,
            String estado,
            LocalDate fechaInicio,
            LocalDate fechaFin,
            UUID profesionalId,
            String profesionalNombre,
            String notas,
            String ultimaAtencionResumen
    ) {}
}
