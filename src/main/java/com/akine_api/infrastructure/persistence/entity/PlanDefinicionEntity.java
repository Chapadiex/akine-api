package com.akine_api.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "plan_definicion")
@Getter
@Setter
@NoArgsConstructor
public class PlanDefinicionEntity {

    @Id
    @Column(length = 40)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_mensual", precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(name = "precio_anual", precision = 10, scale = 2)
    private BigDecimal precioAnual;

    @Column(name = "max_consultorios")
    private Integer maxConsultorios;

    @Column(name = "max_profesionales")
    private Integer maxProfesionales;

    @Column(name = "max_pacientes")
    private Integer maxPacientes;

    @Column(name = "modulo_facturacion", nullable = false)
    private boolean moduloFacturacion;

    @Column(name = "modulo_historia_clinica", nullable = false)
    private boolean moduloHistoriaClinica;

    @Column(name = "modulo_obras_sociales", nullable = false)
    private boolean moduloObrasSociales;

    @Column(name = "modulo_colaboradores", nullable = false)
    private boolean moduloColaboradores;

    @Column(nullable = false)
    private boolean activo;

    @Column(nullable = false)
    private int orden;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
