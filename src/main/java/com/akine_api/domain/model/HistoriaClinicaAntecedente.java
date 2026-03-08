package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class HistoriaClinicaAntecedente {

    private final UUID id;
    private final UUID legajoId;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final String categoryCode;
    private final String catalogItemCode;
    private final String label;
    private final String valueText;
    private final boolean critical;
    private final String notes;
    private final UUID createdByUserId;
    private final UUID updatedByUserId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public HistoriaClinicaAntecedente(UUID id,
                                      UUID legajoId,
                                      UUID consultorioId,
                                      UUID pacienteId,
                                      String categoryCode,
                                      String catalogItemCode,
                                      String label,
                                      String valueText,
                                      boolean critical,
                                      String notes,
                                      UUID createdByUserId,
                                      UUID updatedByUserId,
                                      Instant createdAt,
                                      Instant updatedAt) {
        if (id == null || legajoId == null || consultorioId == null || pacienteId == null) {
            throw new IllegalArgumentException("El antecedente requiere id, legajo, consultorio y paciente");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("El antecedente requiere una etiqueta");
        }
        this.id = id;
        this.legajoId = legajoId;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.categoryCode = categoryCode;
        this.catalogItemCode = catalogItemCode;
        this.label = label;
        this.valueText = valueText;
        this.critical = critical;
        this.notes = notes;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLegajoId() {
        return legajoId;
    }

    public UUID getConsultorioId() {
        return consultorioId;
    }

    public UUID getPacienteId() {
        return pacienteId;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public String getCatalogItemCode() {
        return catalogItemCode;
    }

    public String getLabel() {
        return label;
    }

    public String getValueText() {
        return valueText;
    }

    public boolean isCritical() {
        return critical;
    }

    public String getNotes() {
        return notes;
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
