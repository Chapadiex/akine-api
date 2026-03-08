package com.akine_api.interfaces.api.v1.tratamiento.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TratamientoCatalogUpsertRequest(
        @NotBlank String version,
        @NotNull JsonNode items
) {
}
