package com.akine_api.application.dto.command;

import com.akine_api.domain.model.BoxCapacidadTipo;

import java.util.UUID;

public record UpdateBoxCapacidadCommand(
        UUID id,
        UUID consultorioId,
        BoxCapacidadTipo capacityType,
        Integer capacity
) {}
