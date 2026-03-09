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
@Table(name = "historia_clinica_sesion_evaluaciones")
@Getter
@Setter
@NoArgsConstructor
public class SesionEvaluacionEntity {

    @Id
    private UUID id;

    @Column(name = "sesion_id", nullable = false, unique = true)
    private UUID sesionId;

    @Column(name = "dolor_intensidad")
    private Integer dolorIntensidad;

    @Column(name = "dolor_zona", length = 255)
    private String dolorZona;

    @Column(name = "dolor_lateralidad", length = 50)
    private String dolorLateralidad;

    @Column(name = "dolor_tipo", length = 100)
    private String dolorTipo;

    @Column(name = "dolor_comportamiento", length = 100)
    private String dolorComportamiento;

    @Column(name = "evolución_estado", length = 30)
    private String evolucionEstado;

    @Column(name = "evolucion_nota", length = 500)
    private String evolucionNota;

    @Column(name = "objetivo_sesion", length = 500)
    private String objetivoSesion;

    @Column(name = "limitacion_funcional", length = 1000)
    private String limitacionFuncional;

    @Column(name = "respuesta_paciente", length = 50)
    private String respuestaPaciente;

    @Column(length = 30)
    private String tolerancia;

    @Column(name = "indicaciones_domiciliarias", length = 1000)
    private String indicacionesDomiciliarias;

    @Column(name = "proxima_conducta", length = 100)
    private String proximaConducta;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP")
    private Instant updatedAt;
}
