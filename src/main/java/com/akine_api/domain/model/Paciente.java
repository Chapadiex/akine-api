package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Paciente {

    private final UUID id;
    private final String dni;
    private final Instant createdAt;
    private final UUID userId;
    private final UUID createdByUserId;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDate fechaNacimiento;
    private String sexo;
    private String domicilio;
    private String nacionalidad;
    private String estadoCivil;
    private List<String> profesiones;
    private String obraSocialNombre;
    private String obraSocialPlan;
    private String obraSocialNroAfiliado;
    private boolean activo;
    private Instant updatedAt;

    public Paciente(UUID id,
                    String dni,
                    String nombre,
                    String apellido,
                    String telefono,
                    String email,
                    LocalDate fechaNacimiento,
                    String sexo,
                    String domicilio,
                    String nacionalidad,
                    String estadoCivil,
                    List<String> profesiones,
                    String obraSocialNombre,
                    String obraSocialPlan,
                    String obraSocialNroAfiliado,
                    UUID userId,
                    boolean activo,
                    UUID createdByUserId,
                    Instant createdAt,
                    Instant updatedAt) {
        this.id = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.domicilio = domicilio;
        this.nacionalidad = nacionalidad;
        this.estadoCivil = estadoCivil;
        this.profesiones = profesiones;
        this.obraSocialNombre = obraSocialNombre;
        this.obraSocialPlan = obraSocialPlan;
        this.obraSocialNroAfiliado = obraSocialNroAfiliado;
        this.userId = userId;
        this.activo = activo;
        this.createdByUserId = createdByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateBasic(String nombre, String apellido, String telefono, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.updatedAt = Instant.now();
    }

    public void updateProfile(String nombre,
                              String apellido,
                              String telefono,
                              String email,
                              LocalDate fechaNacimiento,
                              String sexo,
                              String domicilio,
                              String nacionalidad,
                              String estadoCivil,
                              List<String> profesiones,
                              String obraSocialNombre,
                              String obraSocialPlan,
                              String obraSocialNroAfiliado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
        this.domicilio = domicilio;
        this.nacionalidad = nacionalidad;
        this.estadoCivil = estadoCivil;
        this.profesiones = profesiones;
        this.obraSocialNombre = obraSocialNombre;
        this.obraSocialPlan = obraSocialPlan;
        this.obraSocialNroAfiliado = obraSocialNroAfiliado;
        this.updatedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getSexo() { return sexo; }
    public String getDomicilio() { return domicilio; }
    public String getNacionalidad() { return nacionalidad; }
    public String getEstadoCivil() { return estadoCivil; }
    public List<String> getProfesiones() { return profesiones; }
    public String getObraSocialNombre() { return obraSocialNombre; }
    public String getObraSocialPlan() { return obraSocialPlan; }
    public String getObraSocialNroAfiliado() { return obraSocialNroAfiliado; }
    public UUID getUserId() { return userId; }
    public boolean isActivo() { return activo; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
