package com.akine_api.interfaces.api.v1.tratamiento.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record TratamientoCatalogResponse(
        UUID consultorioId,
        String version,
        JsonNode items,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
