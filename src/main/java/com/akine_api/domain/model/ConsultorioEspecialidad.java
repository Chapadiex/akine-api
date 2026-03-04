package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class ConsultorioEspecialidad {

    private final UUID id;
    private final UUID consultorioId;
    private final UUID especialidadId;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public ConsultorioEspecialidad(UUID id, UUID consultorioId, UUID especialidadId, boolean activo, Instant createdAt) {
        this.id = id;
        this.consultorioId = consultorioId;
        this.especialidadId = especialidadId;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void activate() {
        this.activo = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.activo = false;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getEspecialidadId() { return especialidadId; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
