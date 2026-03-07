package com.akine_api.application.dto.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Paciente360TurnosResult(
        String scope,
        long proximosCount,
        long historicosCount,
        long canceladosCount,
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
            UUID id,
            LocalDateTime fechaHoraInicio,
            LocalDateTime fechaHoraFin,
            UUID profesionalId,
            String profesionalNombre,
            String boxNombre,
            String estado,
            String tipoConsulta,
            String motivoConsulta,
            String canalAsignacion,
            String alerta
    ) {}
}
