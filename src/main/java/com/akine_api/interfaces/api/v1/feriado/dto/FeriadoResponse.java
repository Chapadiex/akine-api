package com.akine_api.interfaces.api.v1.feriado.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FeriadoResponse(
        UUID id,
        UUID consultorioId,
        LocalDate fecha,
        String descripcion,
        Instant createdAt
) {}
