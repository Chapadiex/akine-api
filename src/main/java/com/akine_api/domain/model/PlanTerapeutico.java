package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class PlanTerapeutico {

    private final UUID id;
    private final UUID atencionInicialId;
    private final UUID casoAtencionId;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final UUID profesionalId;
    private final PlanTerapeuticoEstado estado;
    private final String observacionesGenerales;
    private final UUID createdByUserId;
    private final UUID updatedByUserId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public PlanTerapeutico(UUID id,
                           UUID atencionInicialId,
                           UUID casoAtencionId,
                           UUID consultorioId,
                           UUID pacienteId,
                           UUID profesionalId,
                           PlanTerapeuticoEstado estado,
                           String observacionesGenerales,
                           UUID createdByUserId,
                           UUID updatedByUserId,
                           Instant createdAt,
                           Instant updatedAt) {
        if (id == null || atencionInicialId == null || consultorioId == null || pacienteId == null || profesionalId == null) {
            throw new IllegalArgumentException("El plan terapeutico requiere ids obligatorios");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El plan terapeutico requiere estado");
        }
        this.id = id;
        this.atencionInicialId = atencionInicialId;
        this.casoAtencionId = casoAtencionId;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.profesionalId = profesionalId;
        this.estado = estado;
        this.observacionesGenerales = observacionesGenerales;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAtencionInicialId() { return atencionInicialId; }
    public UUID getCasoAtencionId() { return casoAtencionId; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getProfesionalId() { return profesionalId; }
    public PlanTerapeuticoEstado getEstado() { return estado; }
    public String getObservacionesGenerales() { return observacionesGenerales; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getUpdatedByUserId() { return updatedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
