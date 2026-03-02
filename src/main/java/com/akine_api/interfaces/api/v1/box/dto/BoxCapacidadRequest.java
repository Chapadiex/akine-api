package com.akine_api.interfaces.api.v1.box.dto;

import com.akine_api.domain.model.BoxCapacidadTipo;
import jakarta.validation.constraints.NotNull;

public record BoxCapacidadRequest(
        @NotNull(message = "El tipo de capacidad es obligatorio")
        BoxCapacidadTipo capacityType,
        Integer capacity
) {}
