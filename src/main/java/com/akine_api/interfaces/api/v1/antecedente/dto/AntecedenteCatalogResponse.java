package com.akine_api.interfaces.api.v1.antecedente.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record AntecedenteCatalogResponse(
        UUID consultorioId,
        String version,
        JsonNode categories,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}

