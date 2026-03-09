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
@Table(name = "historia_clinica_sesion_intervenciones")
@Getter
@Setter
@NoArgsConstructor
public class SesionIntervencionEntity {

    @Id
    private UUID id;

    @Column(name = "sesion_id", nullable = false)
    private UUID sesionId;

    @Column(name = "tratamiento_id", nullable = false, length = 100)
    private String tratamientoId;

    @Column(name = "tratamiento_nombre", nullable = false, length = 255)
    private String tratamientoNombre;

    @Column(length = 255)
    private String técnica;

    @Column(length = 255)
    private String zona;

    @Column(name = "parametros_json", columnDefinition = "TEXT")
    private String parametrosJson;

    @Column(name = "duracion_minutos")
    private Integer duracionMinutos;

    @Column(name = "profesional_id")
    private UUID profesionalId;

    @Column(length = 500)
    private String observaciones;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;
}
