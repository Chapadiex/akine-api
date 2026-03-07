package com.akine_api.application.dto.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Paciente360ObraSocialResult(
        Overview overview,
        Coverage coverage,
        List<AttachmentItem> adjuntos
) {
    public record Overview(
            String obraSocialNombre,
            String plan,
            String nroAfiliado,
            boolean vigente,
            LocalDate fechaVencimiento,
            String tipoCobertura,
            BigDecimal valorCobertura,
            String tipoCoseguro,
            BigDecimal valorCoseguro,
            String observacionesPlan
    ) {}

    public record Coverage(
            Integer prestacionesSinAutorizacion,
            long sesionesUsadasMes,
            Integer sesionesDisponibles,
            boolean autorizacionRequerida,
            String estadoCobertura
    ) {}

    public record AttachmentItem(
            String id,
            String nombre,
            String tipo,
            boolean vigente,
            LocalDateTime fechaCarga
    ) {}
}
