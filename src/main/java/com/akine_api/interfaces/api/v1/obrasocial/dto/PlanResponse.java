package com.akine_api.interfaces.api.v1.obrasocial.dto;

import com.akine_api.domain.model.TipoCobertura;
import com.akine_api.domain.model.TipoCoseguro;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String nombreCorto,
        String nombreCompleto,
        TipoCobertura tipoCobertura,
        BigDecimal valorCobertura,
        TipoCoseguro tipoCoseguro,
        BigDecimal valorCoseguro,
        int prestacionesSinAutorizacion,
        String observaciones,
        boolean activo,
        Instant createdAt,
        Instant updatedAt
) {}

