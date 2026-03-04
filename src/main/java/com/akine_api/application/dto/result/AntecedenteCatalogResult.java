package com.akine_api.application.dto.result;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record AntecedenteCatalogResult(
        UUID consultorioId,
        String version,
        JsonNode categories,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}

