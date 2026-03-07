package com.akine_api.application.dto.result;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Paciente360HistoriaClinicaResult(
        List<ProfesionalOption> profesionales,
        List<Item> items,
        int page,
        int size,
        long total
) {
    public record ProfesionalOption(
            UUID id,
            String nombre
    ) {}

    public record Item(
            String id,
            LocalDateTime fecha,
            UUID profesionalId,
            String profesionalNombre,
            String tipo,
            String resumen,
            String detalle,
            String turnoId,
            Instant ultimaModificacion
    ) {}
}
