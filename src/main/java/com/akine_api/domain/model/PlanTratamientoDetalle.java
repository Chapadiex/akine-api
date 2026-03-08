package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class PlanTratamientoDetalle {

    private final UUID id;
    private final UUID planTerapeuticoId;
    private final String tratamientoId;
    private final String tratamientoNombreSnapshot;
    private final int cantidadSesiones;
    private final String frecuenciaSugerida;
    private final PlanTratamientoCaracter caracterCaso;
    private final LocalDate fechaEstimadaInicio;
    private final boolean requiereAutorizacion;
    private final String observaciones;
    private final String observacionesAdministrativas;
    private final int orderIndex;
    private final Instant createdAt;

    public PlanTratamientoDetalle(UUID id,
                                  UUID planTerapeuticoId,
                                  String tratamientoId,
                                  String tratamientoNombreSnapshot,
                                  int cantidadSesiones,
                                  String frecuenciaSugerida,
                                  PlanTratamientoCaracter caracterCaso,
                                  LocalDate fechaEstimadaInicio,
                                  boolean requiereAutorizacion,
                                  String observaciones,
                                  String observacionesAdministrativas,
                                  int orderIndex,
                                  Instant createdAt) {
        if (id == null || planTerapeuticoId == null) {
            throw new IllegalArgumentException("El tratamiento planificado requiere id y plan");
        }
        if (tratamientoId == null || tratamientoId.isBlank() || tratamientoNombreSnapshot == null || tratamientoNombreSnapshot.isBlank()) {
            throw new IllegalArgumentException("El tratamiento planificado requiere tratamiento valido");
        }
        if (cantidadSesiones <= 0 || caracterCaso == null) {
            throw new IllegalArgumentException("El tratamiento planificado requiere cantidad de sesiones y caracter");
        }
        this.id = id;
        this.planTerapeuticoId = planTerapeuticoId;
        this.tratamientoId = tratamientoId;
        this.tratamientoNombreSnapshot = tratamientoNombreSnapshot;
        this.cantidadSesiones = cantidadSesiones;
        this.frecuenciaSugerida = frecuenciaSugerida;
        this.caracterCaso = caracterCaso;
        this.fechaEstimadaInicio = fechaEstimadaInicio;
        this.requiereAutorizacion = requiereAutorizacion;
        this.observaciones = observaciones;
        this.observacionesAdministrativas = observacionesAdministrativas;
        this.orderIndex = orderIndex;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getPlanTerapeuticoId() { return planTerapeuticoId; }
    public String getTratamientoId() { return tratamientoId; }
    public String getTratamientoNombreSnapshot() { return tratamientoNombreSnapshot; }
    public int getCantidadSesiones() { return cantidadSesiones; }
    public String getFrecuenciaSugerida() { return frecuenciaSugerida; }
    public PlanTratamientoCaracter getCaracterCaso() { return caracterCaso; }
    public LocalDate getFechaEstimadaInicio() { return fechaEstimadaInicio; }
    public boolean isRequiereAutorizacion() { return requiereAutorizacion; }
    public String getObservaciones() { return observaciones; }
    public String getObservacionesAdministrativas() { return observacionesAdministrativas; }
    public int getOrderIndex() { return orderIndex; }
    public Instant getCreatedAt() { return createdAt; }
}
