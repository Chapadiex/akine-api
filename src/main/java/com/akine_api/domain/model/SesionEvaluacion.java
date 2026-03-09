package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class SesionEvaluacion {
    private final UUID id;
    private final UUID sesionId;
    private Integer dolorIntensidad;
    private String dolorZona;
    private String dolorLateralidad;
    private String dolorTipo;
    private String dolorComportamiento;
    private String evolucionEstado;
    private String evolucionNota;
    private String objetivoSesion;
    private String limitacionFuncional;
    private String respuestaPaciente;
    private String tolerancia;
    private String indicacionesDomiciliarias;
    private String proximaConducta;
    private final Instant createdAt;
    private Instant updatedAt;

    public SesionEvaluacion(UUID id, UUID sesionId) {
        this(id, sesionId, null, null, null, null, null, null, null, null, null, null, null, null, null, Instant.now(), Instant.now());
    }

    public SesionEvaluacion(UUID id, UUID sesionId, Integer dolorIntensidad, String dolorZona, String dolorLateralidad, String dolorTipo, String dolorComportamiento, String evolucionEstado, String evolucionNota, String objetivoSesion, String limitacionFuncional, String respuestaPaciente, String tolerancia, String indicacionesDomiciliarias, String proximaConducta, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.sesionId = sesionId;
        this.dolorIntensidad = dolorIntensidad;
        this.dolorZona = dolorZona;
        this.dolorLateralidad = dolorLateralidad;
        this.dolorTipo = dolorTipo;
        this.dolorComportamiento = dolorComportamiento;
        this.evolucionEstado = evolucionEstado;
        this.evolucionNota = evolucionNota;
        this.objetivoSesion = objetivoSesion;
        this.limitacionFuncional = limitacionFuncional;
        this.respuestaPaciente = respuestaPaciente;
        this.tolerancia = tolerancia;
        this.indicacionesDomiciliarias = indicacionesDomiciliarias;
        this.proximaConducta = proximaConducta;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(Integer dolorIntensidad, String dolorZona, String dolorLateralidad, String dolorTipo, String dolorComportamiento, String evolucionEstado, String evolucionNota, String objetivoSesion, String limitacionFuncional, String respuestaPaciente, String tolerancia, String indicacionesDomiciliarias, String proximaConducta) {
        this.dolorIntensidad = dolorIntensidad;
        this.dolorZona = dolorZona;
        this.dolorLateralidad = dolorLateralidad;
        this.dolorTipo = dolorTipo;
        this.dolorComportamiento = dolorComportamiento;
        this.evolucionEstado = evolucionEstado;
        this.evolucionNota = evolucionNota;
        this.objetivoSesion = objetivoSesion;
        this.limitacionFuncional = limitacionFuncional;
        this.respuestaPaciente = respuestaPaciente;
        this.tolerancia = tolerancia;
        this.indicacionesDomiciliarias = indicacionesDomiciliarias;
        this.proximaConducta = proximaConducta;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getSesionId() { return sesionId; }
    public Integer getDolorIntensidad() { return dolorIntensidad; }
    public String getDolorZona() { return dolorZona; }
    public String getDolorLateralidad() { return dolorLateralidad; }
    public String getDolorTipo() { return dolorTipo; }
    public String getDolorComportamiento() { return dolorComportamiento; }
    public String getEvolucionEstado() { return evolucionEstado; }
    public String getEvolucionNota() { return evolucionNota; }
    public String getObjetivoSesion() { return objetivoSesion; }
    public String getLimitacionFuncional() { return limitacionFuncional; }
    public String getRespuestaPaciente() { return respuestaPaciente; }
    public String getTolerancia() { return tolerancia; }
    public String getIndicacionesDomiciliarias() { return indicacionesDomiciliarias; }
    public String getProximaConducta() { return proximaConducta; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
