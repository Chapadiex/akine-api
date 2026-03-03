package com.akine_api.application.dto.result;

import com.akine_api.domain.model.TurnoEstado;

import java.time.Instant;
import java.util.UUID;

public record HistorialEstadoTurnoResult(
        UUID id,
        UUID turnoId,
        TurnoEstado estadoAnterior,
        TurnoEstado estadoNuevo,
        String cambiadoPorUserEmail,
        String motivo,
        Instant createdAt
) {}
