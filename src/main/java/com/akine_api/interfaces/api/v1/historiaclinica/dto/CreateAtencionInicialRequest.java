package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import com.akine_api.domain.model.AtencionInicialTipoIngreso;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateAtencionInicialRequest(
        @NotNull UUID profesionalId,
        @NotNull LocalDateTime fechaHora,
        @NotNull AtencionInicialTipoIngreso tipoIngreso,
        @Size(max = 500) String motivoConsultaBreve,
        @Size(max = 1000) String sintomasPrincipales,
        @Size(max = 255) String tiempoEvolucion,
        @Size(max = 2000) String observaciones,
        @Size(max = 255) String especialidadDerivante,
        @Size(max = 255) String profesionalDerivante,
        LocalDate fechaPrescripcion,
        @Size(max = 100) String diagnosticoCodigo,
        @Size(max = 1000) String diagnosticoObservacion,
        @Size(max = 1000) String observacionesPrescripcion,
        @Valid AtencionInicialEvaluacionRequest evaluacion,
        @Size(max = 2000) String resumenClinicoInicial,
        @Size(max = 2000) String hallazgosRelevantes,
        @Valid List<HistoriaClinicaAntecedenteItemRequest> antecedentes,
        @Size(max = 2000) String planObservacionesGenerales,
        @Valid List<PlanTratamientoDetalleRequest> tratamientos
) {
}
