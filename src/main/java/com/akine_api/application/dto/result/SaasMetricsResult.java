package com.akine_api.application.dto.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record SaasMetricsResult(
        Map<String, Long> totalSuscripciones,
        Map<String, Long> distribucionPlanes,
        MrrResult mrr,
        List<VencimientoProximoResult> vencimientosProximos,
        long nuevasSuscripcionesUltimos30Dias,
        long churnsUltimos30Dias
) {
    public record MrrResult(BigDecimal total, Map<String, BigDecimal> porPlan) {}

    public record VencimientoProximoResult(
            String nroConsultorio,
            String razonSocial,
            LocalDate endDate,
            long diasRestantes
    ) {}
}
