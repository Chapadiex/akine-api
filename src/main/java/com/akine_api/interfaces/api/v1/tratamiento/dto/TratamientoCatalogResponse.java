package com.akine_api.interfaces.api.v1.tratamiento.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record TratamientoCatalogResponse(
        UUID consultorioId,
        String version,
        String monedaNomenclador,
        String pais,
        JsonNode observaciones,
        JsonNode tipos,
        JsonNode categorias,
        JsonNode tratamientos,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
