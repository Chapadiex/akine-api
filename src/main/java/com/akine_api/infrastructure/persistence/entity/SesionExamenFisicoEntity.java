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
@Table(name = "historia_clinica_sesion_examen_fisico")
@Getter
@Setter
@NoArgsConstructor
public class SesionExamenFisicoEntity {

    @Id
    private UUID id;

    @Column(name = "sesion_id", nullable = false, unique = true)
    private UUID sesionId;

    @Column(name = "rango_movimiento_json", columnDefinition = "TEXT")
    private String rangoMovimientoJson;

    @Column(name = "fuerza_muscular_json", columnDefinition = "TEXT")
    private String fuerzaMuscularJson;

    @Column(name = "funcionalidad_nota", length = 1000)
    private String funcionalidadNota;

    @Column(name = "marcha_balance_nota", length = 1000)
    private String marchaBalanceNota;

    @Column(name = "signos_inflamatorios", length = 1000)
    private String signosInflamatorios;

    @Column(name = "observaciones_neuro_resp", length = 1000)
    private String observacionesNeuroResp;

    @Column(name = "tests_medidas_json", columnDefinition = "TEXT")
    private String testsMedidasJson;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
}
