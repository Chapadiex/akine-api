package com.akine_api.interfaces.api.v1.diagnosticomedico.dto;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.UUID;

public record DiagnosticosMedicosResponse(
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
