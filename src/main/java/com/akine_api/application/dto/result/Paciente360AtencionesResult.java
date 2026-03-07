package com.akine_api.application.dto.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Paciente360AtencionesResult(
        long total,
        LocalDateTime ultimaAsistencia,
        List<ProfesionalOption> profesionales,
        List<Item> items,
        int page,
        int size
) {
    public record ProfesionalOption(
            UUID id,
            String nombre
    ) {}

    public record Item(
            UUID id,
            LocalDateTime fecha,
            UUID profesionalId,
            String profesionalNombre,
            String consultorioNombre,
            String boxNombre,
            String estado,
            String resumen,
            UUID turnoId
    ) {}
}
