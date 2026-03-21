package com.akine_api.interfaces.api.v1.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record SaasMetricsResponse(
        Map<String, Long> totalSuscripciones,
        Map<String, Long> distribucionPlanes,
        MrrResponse mrr,
        List<VencimientoProximoResponse> vencimientosProximos,
        long nuevasSuscripcionesUltimos30Dias,
        long churnsUltimos30Dias
) {
    public record MrrResponse(BigDecimal total, Map<String, BigDecimal> porPlan) {}

    public record VencimientoProximoResponse(
            String nroConsultorio,
            String razonSocial,
            LocalDate endDate,
            long diasRestantes
    ) {}
}
