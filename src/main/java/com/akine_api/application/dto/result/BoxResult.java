package com.akine_api.application.dto.result;

import com.akine_api.domain.model.BoxTipo;
import com.akine_api.domain.model.BoxCapacidadTipo;

import java.time.Instant;
import java.util.UUID;

public record BoxResult(
        UUID id,
        UUID consultorioId,
        String nombre,
        String codigo,
        BoxTipo tipo,
        BoxCapacidadTipo capacityType,
        Integer capacity,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}
