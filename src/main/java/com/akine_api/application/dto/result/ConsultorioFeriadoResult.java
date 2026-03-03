package com.akine_api.application.dto.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ConsultorioFeriadoResult(
        UUID id,
        UUID consultorioId,
        LocalDate fecha,
        String descripcion,
        Instant createdAt
) {}
