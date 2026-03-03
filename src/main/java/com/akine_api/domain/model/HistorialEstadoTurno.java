package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class HistorialEstadoTurno {

    private final UUID id;
    private final UUID turnoId;
    private final TurnoEstado estadoAnterior; // null para creación inicial
    private final TurnoEstado estadoNuevo;
    private final UUID cambiadoPorUserId;     // nullable
    private final String motivo;              // nullable
    private final Instant createdAt;

    public HistorialEstadoTurno(UUID id, UUID turnoId, TurnoEstado estadoAnterior,
                                 TurnoEstado estadoNuevo, UUID cambiadoPorUserId,
                                 String motivo, Instant createdAt) {
        if (turnoId == null) {
            throw new IllegalArgumentException("turnoId es obligatorio");
        }
        if (estadoNuevo == null) {
            throw new IllegalArgumentException("estadoNuevo es obligatorio");
        }
        this.id = id;
        this.turnoId = turnoId;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.cambiadoPorUserId = cambiadoPorUserId;
        this.motivo = motivo;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getTurnoId() { return turnoId; }
    public TurnoEstado getEstadoAnterior() { return estadoAnterior; }
    public TurnoEstado getEstadoNuevo() { return estadoNuevo; }
    public UUID getCambiadoPorUserId() { return cambiadoPorUserId; }
    public String getMotivo() { return motivo; }
    public Instant getCreatedAt() { return createdAt; }
}
