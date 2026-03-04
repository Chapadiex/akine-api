package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Empleado {

    private final UUID id;
    private final UUID consultorioId;
    private UUID userId;
    private String nombre;
    private String apellido;
    private String dni;
    private String cargo;
    private String nroLegajo;
    private String email;
    private String telefono;
    private String notasInternas;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private String motivoBaja;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public Empleado(UUID id, UUID consultorioId, UUID userId, String nombre, String apellido, String dni,
                    String cargo, String nroLegajo, String email, String telefono, String notasInternas,
                    LocalDate fechaAlta, LocalDate fechaBaja, String motivoBaja, boolean activo, Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.consultorioId = consultorioId;
        this.userId = userId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.cargo = cargo;
        this.nroLegajo = nroLegajo;
        this.email = email;
        this.telefono = telefono;
        this.notasInternas = notasInternas;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
        this.motivoBaja = motivoBaja;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt != null ? updatedAt : createdAt;
    }

    public void update(String nombre, String apellido, String dni, String cargo,
                       String nroLegajo, String email, String telefono, String notasInternas) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.cargo = cargo;
        this.nroLegajo = nroLegajo;
        this.email = email;
        this.telefono = telefono;
        this.notasInternas = notasInternas;
        this.updatedAt = Instant.now();
    }

    public void inactivate(LocalDate fechaBaja, String motivoBaja) {
        this.activo = false;
        this.fechaBaja = fechaBaja;
        this.motivoBaja = motivoBaja;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        this.activo = true;
        this.fechaBaja = null;
        this.motivoBaja = null;
        this.updatedAt = Instant.now();
    }

    public void linkUser(UUID userId) {
        this.userId = userId;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getUserId() { return userId; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDni() { return dni; }
    public String getCargo() { return cargo; }
    public String getNroLegajo() { return nroLegajo; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getNotasInternas() { return notasInternas; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public LocalDate getFechaBaja() { return fechaBaja; }
    public String getMotivoBaja() { return motivoBaja; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
