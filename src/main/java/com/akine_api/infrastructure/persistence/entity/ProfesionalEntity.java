package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profesionales")
@Getter @Setter @NoArgsConstructor
public class ProfesionalEntity {

    @Id
    private UUID id;

    @Column(name = "consultorio_id")
    private UUID consultorioId;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 50)
    private String matricula;

    @Column(length = 150)
    private String especialidad;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
