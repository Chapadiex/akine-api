package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class PacienteConsultorio {

    private final UUID id;
    private final UUID pacienteId;
    private final UUID consultorioId;
    private final UUID createdByUserId;
    private final Instant createdAt;

    public PacienteConsultorio(UUID id,
                               UUID pacienteId,
                               UUID consultorioId,
                               UUID createdByUserId,
                               Instant createdAt) {
        this.id = id;
        this.pacienteId = pacienteId;
        this.consultorioId = consultorioId;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public Instant getCreatedAt() { return createdAt; }
}
