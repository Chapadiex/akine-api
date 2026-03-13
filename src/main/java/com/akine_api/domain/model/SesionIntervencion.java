package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa una intervención o tratamiento aplicado durante la sesión (Bloque D).
 */
public class SesionIntervencion {
    private final UUID id;
    private final UUID sesionId;
    private final String tratamientoId;
    private final String tratamientoNombre;
    
    private String técnica;
    private String zona;
    private String parametrosJson;
    private Integer duracionMinutos;
    private UUID profesionalId;
    private String observaciones;
    private int orderIndex;
    
    private final Instant createdAt;

    public SesionIntervencion(UUID id, UUID sesionId, String tratamientoId, String tratamientoNombre) {
        this(id, sesionId, tratamientoId, tratamientoNombre, null, null, null, null, null, null, 0, Instant.now());
    }

    public SesionIntervencion(UUID id,
                             UUID sesionId,
                             String tratamientoId,
                             String tratamientoNombre,
                             String técnica,
                             String zona,
                             String parametrosJson,
                             Integer duracionMinutos,
                             UUID profesionalId,
                             String observaciones,
                             int orderIndex,
                             Instant createdAt) {
        if (id == null || sesionId == null || tratamientoId == null || tratamientoNombre == null) {
            throw new IllegalArgumentException("ID, Sesion ID, Tratamiento ID y Nombre son obligatorios");
        }
        this.id = id;
        this.sesionId = sesionId;
        this.tratamientoId = tratamientoId;
        this.tratamientoNombre = tratamientoNombre;
        this.técnica = técnica;
        this.zona = zona;
        this.parametrosJson = parametrosJson;
        this.duracionMinutos = duracionMinutos;
        this.profesionalId = profesionalId;
        this.observaciones = observaciones;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getSesionId() { return sesionId; }
    public String getTratamientoId() { return tratamientoId; }
    public String getTratamientoNombre() { return tratamientoNombre; }
    public String getTécnica() { return técnica; }
    public String getZona() { return zona; }
    public String getParametrosJson() { return parametrosJson; }
    public Integer getDuracionMinutos() { return duracionMinutos; }
    public UUID getProfesionalId() { return profesionalId; }
    public String getObservaciones() { return observaciones; }
    public int getOrderIndex() { return orderIndex; }
    public Instant getCreatedAt() { return createdAt; }
}
