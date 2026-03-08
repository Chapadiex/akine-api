package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class HistoriaClinicaLegajo {

    private final UUID id;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final UUID createdByUserId;
    private final Instant createdAt;
    private UUID updatedByUserId;
    private Instant updatedAt;

    public HistoriaClinicaLegajo(UUID id,
                                 UUID consultorioId,
                                 UUID pacienteId,
                                 UUID createdByUserId,
                                 UUID updatedByUserId,
                                 Instant createdAt,
                                 Instant updatedAt) {
        if (id == null || consultorioId == null || pacienteId == null) {
            throw new IllegalArgumentException("El legajo requiere id, consultorio y paciente");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public void touch(UUID actorUserId) {
        this.updatedByUserId = actorUserId;
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getConsultorioId() {
        return consultorioId;
    }

    public UUID getPacienteId() {
        return pacienteId;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public UUID getUpdatedByUserId() {
        return updatedByUserId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
