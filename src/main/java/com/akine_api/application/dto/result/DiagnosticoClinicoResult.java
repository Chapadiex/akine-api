package com.akine_api.application.dto.result;

import com.akine_api.domain.model.DiagnosticoClinicoEstado;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record DiagnosticoClinicoResult(
        UUID id,
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalId,
        UUID sesionId,
        String codigo,
        String descripcion,
        String diagnosticoTipo,
        String diagnosticoCategoriaCodigo,
        String diagnosticoCategoriaNombre,
        String diagnosticoSubcategoria,
        String diagnosticoRegionAnatomica,
        DiagnosticoClinicoEstado estado,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        String notas,
        Instant createdAt,
        Instant updatedAt
) {}
