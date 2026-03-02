package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "boxes")
@Getter @Setter @NoArgsConstructor
public class BoxEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id", nullable = false)
    private UUID consultorioId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 50)
    private String codigo;

    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(name = "capacity_type", nullable = false, length = 20)
    private String capacityType;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
