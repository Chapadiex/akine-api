package com.akine_api.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AtencionInicialEvaluacion {

    private final UUID id;
    private final UUID atencionInicialId;
    private final BigDecimal peso;
    private final BigDecimal altura;
    private final BigDecimal imc;
    private final String presionArterial;
    private final Integer frecuenciaCardiaca;
    private final Integer saturacion;
    private final BigDecimal temperatura;
    private final String observaciones;
    private final Instant createdAt;
    private final Instant updatedAt;

    public AtencionInicialEvaluacion(UUID id,
                                     UUID atencionInicialId,
                                     BigDecimal peso,
                                     BigDecimal altura,
                                     BigDecimal imc,
                                     String presionArterial,
                                     Integer frecuenciaCardiaca,
                                     Integer saturacion,
                                     BigDecimal temperatura,
                                     String observaciones,
                                     Instant createdAt,
                                     Instant updatedAt) {
        if (id == null || atencionInicialId == null) {
            throw new IllegalArgumentException("La evaluacion inicial requiere id y atencion");
        }
        this.id = id;
        this.atencionInicialId = atencionInicialId;
        this.peso = peso;
        this.altura = altura;
        this.imc = imc;
        this.presionArterial = presionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.saturacion = saturacion;
        this.temperatura = temperatura;
        this.observaciones = observaciones;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public UUID getId() { return id; }
    public UUID getAtencionInicialId() { return atencionInicialId; }
    public BigDecimal getPeso() { return peso; }
    public BigDecimal getAltura() { return altura; }
    public BigDecimal getImc() { return imc; }
    public String getPresionArterial() { return presionArterial; }
    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public Integer getSaturacion() { return saturacion; }
    public BigDecimal getTemperatura() { return temperatura; }
    public String getObservaciones() { return observaciones; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
