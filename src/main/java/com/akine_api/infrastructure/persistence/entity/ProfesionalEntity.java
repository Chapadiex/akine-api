package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
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

    @Column(name = "nro_documento", length = 20)
    private String nroDocumento;

    @Column(nullable = false, length = 50)
    private String matricula;

    @Column(length = 150)
    private String especialidad;

    @Column(length = 1000)
    private String especialidades;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(length = 255)
    private String domicilio;

    @Column(name = "foto_perfil_url", length = 500)
    private String fotoPerfilUrl;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    @Column(name = "motivo_baja", length = 255)
    private String motivoBaja;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
