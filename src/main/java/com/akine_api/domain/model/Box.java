package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Box {

    private final UUID id;
    private final UUID consultorioId;
    private String nombre;
    private String codigo;
    private BoxTipo tipo;
    private BoxCapacidadTipo capacityType;
    private Integer capacity;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public Box(UUID id, UUID consultorioId, String nombre, String codigo,
               BoxTipo tipo, BoxCapacidadTipo capacityType, Integer capacity,
               boolean activo, Instant createdAt) {
        this.id = id;
        this.consultorioId = consultorioId;
        this.nombre = nombre;
        this.codigo = codigo;
        this.tipo = tipo;
        this.capacityType = capacityType == null ? BoxCapacidadTipo.UNLIMITED : capacityType;
        this.capacity = capacity;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String nombre, String codigo, BoxTipo tipo) {
        update(nombre, codigo, tipo, null);
    }

    public void update(String nombre, String codigo, BoxTipo tipo, Boolean activo) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.tipo = tipo;
        if (activo != null) {
            this.activo = activo;
        }
        this.updatedAt = Instant.now();
    }

    public void updateCapacidad(BoxCapacidadTipo capacityType, Integer capacity) {
        this.capacityType = capacityType == null ? BoxCapacidadTipo.UNLIMITED : capacityType;
        if (this.capacityType == BoxCapacidadTipo.UNLIMITED) {
            this.capacity = null;
        } else {
            this.capacity = capacity;
        }
        this.updatedAt = Instant.now();
    }

    public void inactivate() {
        this.activo = false;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.activo = true;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public String getNombre() { return nombre; }
    public String getCodigo() { return codigo; }
    public BoxTipo getTipo() { return tipo; }
    public BoxCapacidadTipo getCapacityType() { return capacityType; }
    public Integer getCapacity() { return capacity; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
