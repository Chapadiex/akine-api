package com.akine_api.application.dto.result;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record TratamientoCatalogResult(
        UUID consultorioId,
        String version,
        JsonNode items,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
