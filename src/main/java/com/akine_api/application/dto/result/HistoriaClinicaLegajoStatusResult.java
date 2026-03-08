package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record HistoriaClinicaLegajoStatusResult(
        boolean exists,
        UUID legajoId,
        Instant createdAt,
        Instant updatedAt
) {}
