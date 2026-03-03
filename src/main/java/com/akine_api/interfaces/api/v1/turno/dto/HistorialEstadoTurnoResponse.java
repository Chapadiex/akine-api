package com.akine_api.interfaces.api.v1.turno.dto;

import java.time.Instant;
import java.util.UUID;

public record HistorialEstadoTurnoResponse(
        UUID id,
        UUID turnoId,
        String estadoAnterior,
        String estadoNuevo,
        String cambiadoPorUserEmail,
        String motivo,
        Instant createdAt
) {}
