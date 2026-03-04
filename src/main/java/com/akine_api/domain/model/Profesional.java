package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class Profesional {

    private final UUID id;
    private final UUID consultorioId;
    private String nombre;
    private String apellido;
    private String nroDocumento;
    private String matricula;
    private String especialidad;
    private String especialidades;
    private String email;
    private String telefono;
    private String domicilio;
    private String fotoPerfilUrl;
    private LocalDate fechaAlta;
    private LocalDate fechaBaja;
    private String motivoBaja;
    private boolean activo;
    private final Instant createdAt;
    private Instant updatedAt;

    public Profesional(UUID id, UUID consultorioId, String nombre, String apellido,
                       String nroDocumento, String matricula, String especialidad,
                       String especialidades, String email, String telefono,
                       String domicilio, String fotoPerfilUrl,
                       LocalDate fechaAlta, LocalDate fechaBaja, String motivoBaja,
                       boolean activo, Instant createdAt) {
        this.id = id;
        this.consultorioId = consultorioId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nroDocumento = nroDocumento;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.especialidades = especialidades;
        this.email = email;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.fotoPerfilUrl = fotoPerfilUrl;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = fechaBaja;
        this.motivoBaja = motivoBaja;
        this.activo = activo;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public void update(String nombre, String apellido, String nroDocumento, String matricula,
                       String especialidad, String especialidades, String email, String telefono,
                       String domicilio, String fotoPerfilUrl) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.nroDocumento = nroDocumento;
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.especialidades = especialidades;
        this.email = email;
        this.telefono = telefono;
        this.domicilio = domicilio;
        this.fotoPerfilUrl = fotoPerfilUrl;
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

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getNroDocumento() { return nroDocumento; }
    public String getMatricula() { return matricula; }
    public String getEspecialidad() { return especialidad; }
    public String getEspecialidades() { return especialidades; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getDomicilio() { return domicilio; }
    public String getFotoPerfilUrl() { return fotoPerfilUrl; }
    public LocalDate getFechaAlta() { return fechaAlta; }
    public LocalDate getFechaBaja() { return fechaBaja; }
    public String getMotivoBaja() { return motivoBaja; }
    public boolean isActivo() { return activo; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
