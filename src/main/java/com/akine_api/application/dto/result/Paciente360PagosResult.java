package com.akine_api.application.dto.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record Paciente360PagosResult(
        Summary summary,
        List<Item> items,
        List<ConciliationItem> conciliacion,
        int page,
        int size,
        long total
) {
    public record Summary(
            BigDecimal saldoPendiente,
            BigDecimal totalCobrado,
            BigDecimal ultimoPagoMonto,
            LocalDateTime ultimoPagoFecha,
            BigDecimal deudaVencida
    ) {}

    public record Item(
            UUID id,
            LocalDateTime fecha,
            String concepto,
            BigDecimal monto,
            String tipo,
            String estado,
            String medioPago,
            String comprobante
    ) {}

    public record ConciliationItem(
            String id,
            String estado,
            String detalle
    ) {}
}
