package com.akine_api.application.dto.result;

import java.util.List;
import java.util.UUID;

public record HistoriaClinicaWorkspaceResult(
        List<ProfesionalOption> profesionales,
        List<HistoriaClinicaWorkspaceItem> items,
        int page,
        int size,
        long total
) {
    public record ProfesionalOption(UUID id, String nombre) {}
}
