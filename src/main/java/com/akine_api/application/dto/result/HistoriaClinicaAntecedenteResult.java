package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record HistoriaClinicaAntecedenteResult(
        UUID id,
        String categoryCode,
        String catalogItemCode,
        String label,
        String valueText,
        boolean critical,
        String notes,
        Instant updatedAt
) {}
