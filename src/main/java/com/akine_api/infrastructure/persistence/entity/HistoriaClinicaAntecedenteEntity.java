package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "historia_clinica_antecedentes")
@Getter
@Setter
@NoArgsConstructor
public class HistoriaClinicaAntecedenteEntity {

    @Id
    private UUID id;

    @Column(name = "legajo_id", nullable = false)
    private UUID legajoId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(name = "paciente_id", nullable = false)
    private UUID pacienteId;

    @Column(name = "category_code", length = 100)
    private String categoryCode;

    @Column(name = "catalog_item_code", length = 100)
    private String catalogItemCode;

    @Column(name = "label", nullable = false, length = 255)
    private String label;

    @Column(name = "value_text", length = 2000)
    private String valueText;

    @Column(name = "critical", nullable = false)
    private boolean critical;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "updated_by_user_id")
    private UUID updatedByUserId;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
}
