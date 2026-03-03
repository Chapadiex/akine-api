package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class ConsultorioFeriado {

    private final UUID id;
    private final UUID consultorioId;
    private final LocalDate fecha;
    private final String descripcion;
    private final Instant createdAt;

    public ConsultorioFeriado(UUID id, UUID consultorioId, LocalDate fecha,
                               String descripcion, Instant createdAt) {
        if (consultorioId == null) {
            throw new IllegalArgumentException("consultorioId es obligatorio");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("fecha es obligatoria");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.createdAt = createdAt;
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public LocalDate getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public Instant getCreatedAt() { return createdAt; }
}
