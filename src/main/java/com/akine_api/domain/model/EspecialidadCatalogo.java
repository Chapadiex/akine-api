package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class EspecialidadCatalogo {

    private final UUID id;
    private String nombre;
    private String slug;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public EspecialidadCatalogo(UUID id, String nombre, String slug, boolean activo, Instant createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.slug = slug;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void rename(String nombre, String slug) {
        this.nombre = nombre;
        this.slug = slug;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.activo = true;
        this.updatedAt = Instant.now();
    }

    public void deactivate() {
        this.activo = false;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getNombre() { return nombre; }
    public String getSlug() { return slug; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
