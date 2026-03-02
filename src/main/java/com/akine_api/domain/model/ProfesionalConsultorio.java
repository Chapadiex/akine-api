package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ProfesionalConsultorio {

    private final UUID id;
    private final UUID profesionalId;
    private final UUID consultorioId;
    private boolean activo;
    private final Instant createdAt;

    public ProfesionalConsultorio(UUID id, UUID profesionalId, UUID consultorioId, boolean activo, Instant createdAt) {
        this.id = id;
        this.profesionalId = profesionalId;
        this.consultorioId = consultorioId;
        this.activo = activo;
        this.createdAt = createdAt;
    }

    public void inactivate() {
        this.activo = false;
    }

    public UUID getId() { return id; }
    public UUID getProfesionalId() { return profesionalId; }
    public UUID getConsultorioId() { return consultorioId; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
}
