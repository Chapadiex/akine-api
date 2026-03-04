package com.akine_api.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ObraSocialPlan {

    private final UUID id;
    private String nombreCorto;
    private String nombreCortoNorm;
    private String nombreCompleto;
    private TipoCobertura tipoCobertura;
    private BigDecimal valorCobertura;
    private TipoCoseguro tipoCoseguro;
    private BigDecimal valorCoseguro;
    private int prestacionesSinAutorizacion;
    private String observaciones;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public ObraSocialPlan(UUID id,
                          String nombreCorto,
                          String nombreCortoNorm,
                          String nombreCompleto,
                          TipoCobertura tipoCobertura,
                          BigDecimal valorCobertura,
                          TipoCoseguro tipoCoseguro,
                          BigDecimal valorCoseguro,
                          int prestacionesSinAutorizacion,
                          String observaciones,
                          boolean activo,
                          Instant createdAt) {
        this.id = id;
        this.nombreCorto = nombreCorto;
        this.nombreCortoNorm = nombreCortoNorm;
        this.nombreCompleto = nombreCompleto;
        this.tipoCobertura = tipoCobertura;
        this.valorCobertura = valorCobertura;
        this.tipoCoseguro = tipoCoseguro;
        this.valorCoseguro = valorCoseguro;
        this.prestacionesSinAutorizacion = prestacionesSinAutorizacion;
        this.observaciones = observaciones;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String nombreCorto,
                       String nombreCortoNorm,
                       String nombreCompleto,
                       TipoCobertura tipoCobertura,
                       BigDecimal valorCobertura,
                       TipoCoseguro tipoCoseguro,
                       BigDecimal valorCoseguro,
                       int prestacionesSinAutorizacion,
                       String observaciones,
                       boolean activo) {
        this.nombreCorto = nombreCorto;
        this.nombreCortoNorm = nombreCortoNorm;
        this.nombreCompleto = nombreCompleto;
        this.tipoCobertura = tipoCobertura;
        this.valorCobertura = valorCobertura;
        this.tipoCoseguro = tipoCoseguro;
        this.valorCoseguro = valorCoseguro;
        this.prestacionesSinAutorizacion = prestacionesSinAutorizacion;
        this.observaciones = observaciones;
        this.activo = activo;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getNombreCorto() { return nombreCorto; }
    public String getNombreCortoNorm() { return nombreCortoNorm; }
    public String getNombreCompleto() { return nombreCompleto; }
    public TipoCobertura getTipoCobertura() { return tipoCobertura; }
    public BigDecimal getValorCobertura() { return valorCobertura; }
    public TipoCoseguro getTipoCoseguro() { return tipoCoseguro; }
    public BigDecimal getValorCoseguro() { return valorCoseguro; }
    public int getPrestacionesSinAutorizacion() { return prestacionesSinAutorizacion; }
    public String getObservaciones() { return observaciones; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}

