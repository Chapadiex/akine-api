package com.akine_api.interfaces.api.v1.obrasocial.dto;

import com.akine_api.domain.model.TipoCobertura;
import com.akine_api.domain.model.TipoCoseguro;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record PlanRequest(
        UUID id,

        @NotBlank(message = "El nombre corto es obligatorio")
        @Size(max = 60, message = "El nombre corto no puede superar 60 caracteres")
        String nombreCorto,

        @NotBlank(message = "El nombre completo es obligatorio")
        @Size(max = 120, message = "El nombre completo no puede superar 120 caracteres")
        String nombreCompleto,

        @NotNull(message = "El tipo de cobertura es obligatorio")
        TipoCobertura tipoCobertura,

        @NotNull(message = "El valor de cobertura es obligatorio")
        @DecimalMin(value = "0.00", message = "El valor de cobertura no puede ser negativo")
        BigDecimal valorCobertura,

        @NotNull(message = "El tipo de coseguro es obligatorio")
        TipoCoseguro tipoCoseguro,

        @NotNull(message = "El valor de coseguro es obligatorio")
        @DecimalMin(value = "0.00", message = "El valor de coseguro no puede ser negativo")
        BigDecimal valorCoseguro,

        @NotNull(message = "La cantidad de prestaciones sin autorizacion es obligatoria")
        @Min(value = 0, message = "Prestaciones sin autorizacion no puede ser negativo")
        Integer prestacionesSinAutorizacion,

        @Size(max = 1000, message = "Las observaciones no pueden superar 1000 caracteres")
        String observaciones,

        Boolean activo
) {}

