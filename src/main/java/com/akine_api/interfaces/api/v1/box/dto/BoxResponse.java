package com.akine_api.interfaces.api.v1.box.dto;

import com.akine_api.domain.model.BoxTipo;
import com.akine_api.domain.model.BoxCapacidadTipo;

import java.time.Instant;
import java.util.UUID;

public record BoxResponse(
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
