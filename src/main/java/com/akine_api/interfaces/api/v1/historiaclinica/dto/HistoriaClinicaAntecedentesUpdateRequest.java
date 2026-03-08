package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import jakarta.validation.Valid;

import java.util.List;

public record HistoriaClinicaAntecedentesUpdateRequest(
        @Valid List<HistoriaClinicaAntecedenteItemRequest> antecedentes
) {}
