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
import java.util.UUID;

@Entity
@Table(name = "historia_clinica_atencion_inicial_evaluacion")
@Getter
@Setter
@NoArgsConstructor
public class AtencionInicialEvaluacionEntity {

    @Id
    private UUID id;

    @Column(name = "atencion_inicial_id", nullable = false)
    private UUID atencionInicialId;

    private BigDecimal peso;
    private BigDecimal altura;
    private BigDecimal imc;

    @Column(name = "presion_arterial", length = 30)
    private String presionArterial;

    @Column(name = "frecuencia_cardiaca")
    private Integer frecuenciaCardiaca;

    private Integer saturacion;
    private BigDecimal temperatura;

    @Column(length = 1000)
    private String observaciones;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
