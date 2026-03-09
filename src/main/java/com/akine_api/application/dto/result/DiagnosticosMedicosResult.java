package com.akine_api.application.dto.result;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record DiagnosticosMedicosResult(
        UUID consultorioId,
        String version,
        String pais,
        String idioma,
        JsonNode tipos,
        JsonNode categorias,
        JsonNode diagnosticos,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        String updatedBy
) {
}
