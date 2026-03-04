package com.akine_api.application.dto.command;

import com.akine_api.domain.model.ObraSocialEstado;
import com.akine_api.domain.model.TipoCobertura;
import com.akine_api.domain.model.TipoCoseguro;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PlanCommand(
        UUID id,
        String nombreCorto,
        String nombreCompleto,
        TipoCobertura tipoCobertura,
        BigDecimal valorCobertura,
        TipoCoseguro tipoCoseguro,
        BigDecimal valorCoseguro,
        Integer prestacionesSinAutorizacion,
        String observaciones,
        Boolean activo
) {}


