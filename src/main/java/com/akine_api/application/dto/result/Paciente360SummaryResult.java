package com.akine_api.application.dto.result;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record Paciente360SummaryResult(
        Kpis kpis,
        List<AlertItem> alertas,
        List<ActionItem> proximasAcciones,
        List<ActivityItem> actividadReciente
) {
    public record Kpis(
            LocalDateTime proximoTurnoFecha,
            String proximoTurnoProfesional,
            String proximoTurnoEstado,
            LocalDateTime ultimaAtencionFecha,
            String ultimaAtencionProfesional,
            String ultimaAtencionResumen,
            int diagnosticosActivos,
            long sesionesMes,
            String coberturaEstado,
            BigDecimal saldoPendiente
    ) {}

    public record AlertItem(
            String tipo,
            String mensaje,
            String route
    ) {}

    public record ActionItem(
            String tipo,
            String etiqueta,
            String route,
            LocalDateTime fechaReferencia
    ) {}

    public record ActivityItem(
            String id,
            String tipo,
            String titulo,
            String detalle,
            LocalDateTime fecha,
            String route
    ) {}
}
