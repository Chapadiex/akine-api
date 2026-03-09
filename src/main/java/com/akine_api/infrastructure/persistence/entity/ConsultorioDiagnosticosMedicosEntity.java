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
@Table(name = "consultorio_diagnosticos_medicos")
@Getter
@Setter
@NoArgsConstructor
public class ConsultorioDiagnosticosMedicosEntity {

    @Id
    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(nullable = false, length = 20)
    private String version;

    @Column(name = "maestro_json", nullable = false, columnDefinition = "TEXT")
    private String maestroJson;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by", nullable = false, length = 120)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "updated_by", nullable = false, length = 120)
    private String updatedBy;
}
