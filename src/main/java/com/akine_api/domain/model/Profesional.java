package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Profesional {

    private final UUID id;
    private final UUID consultorioId;
    private String nombre;
    private String apellido;
    private String matricula;
    private String especialidad;
    private String email;
    private String telefono;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public Profesional(UUID id, UUID consultorioId, String nombre, String apellido,
                       String matricula, String especialidad, String email, String telefono,
                       boolean activo, Instant createdAt) {
        this.id = id;
        this.consultorioId = consultorioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String nombre, String apellido, String matricula,
                       String especialidad, String email, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.email = email;
        this.telefono = telefono;
        this.updatedAt = Instant.now();
    }

    public void inactivate() {
        this.activo = false;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getMatricula() { return matricula; }
    public String getEspecialidad() { return especialidad; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
