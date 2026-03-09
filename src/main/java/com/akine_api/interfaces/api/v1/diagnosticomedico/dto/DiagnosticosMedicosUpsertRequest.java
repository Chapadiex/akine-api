package com.akine_api.interfaces.api.v1.diagnosticomedico.dto;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiagnosticosMedicosUpsertRequest(
        @NotBlank String version,
        @NotBlank String pais,
        @NotBlank String idioma,
        @NotNull JsonNode tipos,
        @NotNull JsonNode categorias,
        @NotNull JsonNode diagnosticos
) {
}
