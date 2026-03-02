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
@Table(name = "profesional_consultorios")
@Getter
@Setter
@NoArgsConstructor
public class ProfesionalConsultorioEntity {

    @Id
    private UUID id;

    @Column(name = "profesional_id", nullable = false)
    private UUID profesionalId;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
