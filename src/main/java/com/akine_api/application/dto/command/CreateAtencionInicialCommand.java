package com.akine_api.application.dto.command;

import com.akine_api.domain.model.AtencionInicialTipoIngreso;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateAtencionInicialCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalId,
        LocalDateTime fechaHora,
        AtencionInicialTipoIngreso tipoIngreso,
        String motivoConsultaBreve,
        String sintomasPrincipales,
        String tiempoEvolucion,
        String observaciones,
        String especialidadDerivante,
        String profesionalDerivante,
        LocalDate fechaPrescripcion,
        String diagnosticoCodigo,
        String diagnosticoObservacion,
        String observacionesPrescripcion,
        AtencionInicialEvaluacionCommand evaluacion,
        String resumenClinicoInicial,
        String hallazgosRelevantes,
        List<HistoriaClinicaAntecedenteItemCommand> antecedentes,
        String planObservacionesGenerales,
        List<PlanTratamientoDetalleCommand> tratamientos,
        UUID actorUserId
) {
}
