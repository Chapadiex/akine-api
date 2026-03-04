package com.akine_api.interfaces.api.v1.antecedente.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AntecedenteCatalogUpsertRequest(
        @NotBlank String version,
        @NotNull JsonNode categories
) {
}

