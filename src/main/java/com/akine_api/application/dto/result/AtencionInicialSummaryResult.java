package com.akine_api.application.dto.result;

import com.akine_api.domain.model.AtencionInicialTipoIngreso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AtencionInicialSummaryResult(
        UUID id,
        UUID profesionalId,
        String profesionalNombre,
        LocalDateTime fechaHora,
        AtencionInicialTipoIngreso tipoIngreso,
        String motivoConsultaBreve,
        String sintomasPrincipales,
        String tiempoEvolucion,
        String observaciones,
        String especialidadDerivante,
        String profesionalDerivante,
        LocalDate fechaPrescripcion,
        String diagnosticoTexto,
        String observacionesPrescripcion,
        String resumenClinicoInicial,
        String hallazgosRelevantes
) {
}
