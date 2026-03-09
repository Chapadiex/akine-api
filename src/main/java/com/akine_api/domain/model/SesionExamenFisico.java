package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa el examen físico detallado de una sesión (Bloque C).
 */
public class SesionExamenFisico {
    private final UUID id;
    private final UUID sesionId;
    
    private String rangoMovimientoJson;
    private String fuerzaMuscularJson;
    private String funcionalidadNota;
    private String marchaBalanceNota;
    private String signosInflamatorios;
    private String observacionesNeuroResp;
    private String testsMedidasJson;
    
    private final Instant createdAt;
    private Instant updatedAt;

    public SesionExamenFisico(UUID id, UUID sesionId) {
        this(id, sesionId, null, null, null, null, null, null, null, Instant.now(), Instant.now());
    }

    public SesionExamenFisico(UUID id,
                               UUID sesionId,
                               String rangoMovimientoJson,
                               String fuerzaMuscularJson,
                               String funcionalidadNota,
                               String marchaBalanceNota,
                               String signosInflamatorios,
                               String observacionesNeuroResp,
                               String testsMedidasJson,
                               Instant createdAt,
                               Instant updatedAt) {
        if (id == null || sesionId == null) {
            throw new IllegalArgumentException("ID y Sesion ID son obligatorios");
        }
        this.id = id;
        this.sesionId = sesionId;
        this.rangoMovimientoJson = rangoMovimientoJson;
        this.fuerzaMuscularJson = fuerzaMuscularJson;
        this.funcionalidadNota = funcionalidadNota;
        this.marchaBalanceNota = marchaBalanceNota;
        this.signosInflamatorios = signosInflamatorios;
        this.observacionesNeuroResp = observacionesNeuroResp;
        this.testsMedidasJson = testsMedidasJson;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public void update(String rangoMovimientoJson,
                       String fuerzaMuscularJson,
                       String funcionalidadNota,
                       String marchaBalanceNota,
                       String signosInflamatorios,
                       String observacionesNeuroResp,
                       String testsMedidasJson) {
        this.rangoMovimientoJson = rangoMovimientoJson;
        this.fuerzaMuscularJson = fuerzaMuscularJson;
        this.funcionalidadNota = funcionalidadNota;
        this.marchaBalanceNota = marchaBalanceNota;
        this.signosInflamatorios = signosInflamatorios;
        this.observacionesNeuroResp = observacionesNeuroResp;
        this.testsMedidasJson = testsMedidasJson;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getSesionId() { return sesionId; }
    public String getRangoMovimientoJson() { return rangoMovimientoJson; }
    public String getFuerzaMuscularJson() { return fuerzaMuscularJson; }
    public String getFuncionalidadNota() { return funcionalidadNota; }
    public String getMarchaBalanceNota() { return marchaBalanceNota; }
    public String getSignosInflamatorios() { return signosInflamatorios; }
    public String getObservacionesNeuroResp() { return observacionesNeuroResp; }
    public String getTestsMedidasJson() { return testsMedidasJson; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
