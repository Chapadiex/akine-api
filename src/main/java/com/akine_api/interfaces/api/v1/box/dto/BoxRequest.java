package com.akine_api.interfaces.api.v1.box.dto;

import com.akine_api.domain.model.BoxCapacidadTipo;
import com.akine_api.domain.model.BoxTipo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BoxRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
        String nombre,

        @Size(max = 50, message = "El codigo no puede superar 50 caracteres")
        String codigo,

        @NotNull(message = "El tipo es obligatorio")
        BoxTipo tipo,

        @NotNull(message = "El tipo de capacidad es obligatorio")
        BoxCapacidadTipo capacityType,

        @Min(value = 1, message = "La capacidad debe ser mayor o igual a 1")
        Integer capacity,

        Boolean activo
) {}
