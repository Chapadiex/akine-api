package com.akine_api.application.dto.command;

public record HistoriaClinicaAntecedenteItemCommand(
        String categoryCode,
        String catalogItemCode,
        String label,
        String valueText,
        boolean critical,
        String notes
) {}
