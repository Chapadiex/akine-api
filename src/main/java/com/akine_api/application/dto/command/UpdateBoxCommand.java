package com.akine_api.application.dto.command;

import com.akine_api.domain.model.BoxCapacidadTipo;
import com.akine_api.domain.model.BoxTipo;

import java.util.UUID;

public record UpdateBoxCommand(
        UUID id,
        UUID consultorioId,
        String nombre,
        String codigo,
        BoxTipo tipo,
        BoxCapacidadTipo capacityType,
        Integer capacity,
        Boolean activo
) {}
