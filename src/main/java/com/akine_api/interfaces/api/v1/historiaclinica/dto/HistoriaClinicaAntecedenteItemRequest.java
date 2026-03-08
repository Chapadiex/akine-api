package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record HistoriaClinicaAntecedenteItemRequest(
        @Size(max = 100) String categoryCode,
        @Size(max = 100) String catalogItemCode,
        @NotBlank @Size(max = 255) String label,
        @Size(max = 2000) String valueText,
        boolean critical,
        @Size(max = 1000) String notes
) {}
