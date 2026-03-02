package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pacientes")
@Getter
@Setter
@NoArgsConstructor
public class PacienteEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 30)
    private String telefono;

    @Column(length = 255)
    private String email;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 30)
    private String sexo;

    @Column(length = 255)
    private String domicilio;

    @Column(length = 100)
    private String nacionalidad;

    @Column(name = "estado_civil", length = 50)
    private String estadoCivil;

    @Column(length = 100)
    private String profesion;

    @Column(name = "obra_social_nombre", length = 150)
    private String obraSocialNombre;

    @Column(name = "obra_social_plan", length = 100)
    private String obraSocialPlan;

    @Column(name = "obra_social_nro_afiliado", length = 100)
    private String obraSocialNroAfiliado;

    @Column(name = "user_id")
    private UUID userId;

    @Column(nullable = false)
    private boolean activo;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
