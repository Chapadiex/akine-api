package com.akine_api.domain.model;

import com.akine_api.domain.exception.HistoriaClinicaConflictException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class DiagnosticoClinico {

    private final UUID id;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final UUID createdByUserId;
    private final Instant createdAt;
    private UUID profesionalId;
    private UUID sesionId;
    private UUID casoAtencionId;
    private String codigo;
    private String descripcion;
    private String diagnosticoTipo;
    private String diagnosticoCategoriaCodigo;
    private String diagnosticoCategoriaNombre;
    private String diagnosticoSubcategoria;
    private String diagnosticoRegionAnatomica;
    private DiagnosticoClinicoEstado estado;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String notas;
    private UUID updatedByUserId;
    private Instant updatedAt;

    public DiagnosticoClinico(UUID id,
                              UUID consultorioId,
                              UUID pacienteId,
                              UUID profesionalId,
                              UUID sesionId,
                              UUID casoAtencionId,
                              String codigo,
                              String descripcion,
                              String diagnosticoTipo,
                              String diagnosticoCategoriaCodigo,
                              String diagnosticoCategoriaNombre,
                              String diagnosticoSubcategoria,
                              String diagnosticoRegionAnatomica,
                              DiagnosticoClinicoEstado estado,
                              LocalDate fechaInicio,
                              LocalDate fechaFin,
                              String notas,
                              UUID createdByUserId,
                              UUID updatedByUserId,
                              Instant createdAt,
                              Instant updatedAt) {
        if (consultorioId == null || pacienteId == null || profesionalId == null) {
            throw new IllegalArgumentException("Consultorio, paciente y profesional son obligatorios");
        }
        if (descripcion == null || descripcion.isBlank() || fechaInicio == null || estado == null) {
            throw new IllegalArgumentException("Descripcion, fecha de inicio y estado son obligatorios");
        }
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.profesionalId = profesionalId;
        this.sesionId = sesionId;
        this.casoAtencionId = casoAtencionId;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.diagnosticoTipo = diagnosticoTipo;
        this.diagnosticoCategoriaCodigo = diagnosticoCategoriaCodigo;
        this.diagnosticoCategoriaNombre = diagnosticoCategoriaNombre;
        this.diagnosticoSubcategoria = diagnosticoSubcategoria;
        this.diagnosticoRegionAnatomica = diagnosticoRegionAnatomica;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.notas = notas;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public void update(UUID profesionalId,
                       UUID sesionId,
                       String codigo,
                       String descripcion,
                       String diagnosticoTipo,
                       String diagnosticoCategoriaCodigo,
                       String diagnosticoCategoriaNombre,
                       String diagnosticoSubcategoria,
                       String diagnosticoRegionAnatomica,
                       LocalDate fechaInicio,
                       String notas,
                       UUID updatedByUserId) {
        assertEditable();
        if (profesionalId == null || descripcion == null || descripcion.isBlank() || fechaInicio == null) {
            throw new IllegalArgumentException("Profesional, descripcion y fecha de inicio son obligatorios");
        }
        this.profesionalId = profesionalId;
        this.sesionId = sesionId;
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.diagnosticoTipo = diagnosticoTipo;
        this.diagnosticoCategoriaCodigo = diagnosticoCategoriaCodigo;
        this.diagnosticoCategoriaNombre = diagnosticoCategoriaNombre;
        this.diagnosticoSubcategoria = diagnosticoSubcategoria;
        this.diagnosticoRegionAnatomica = diagnosticoRegionAnatomica;
        this.fechaInicio = fechaInicio;
        this.notas = notas;
        this.updatedByUserId = updatedByUserId;
        this.updatedAt = Instant.now();
    }

    public void resolve(LocalDate fechaFin, UUID updatedByUserId) {
        assertEditable();
        if (fechaFin == null || fechaFin.isBefore(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de resolucion es invalida");
        }
        this.estado = DiagnosticoClinicoEstado.RESUELTO;
        this.fechaFin = fechaFin;
        this.updatedByUserId = updatedByUserId;
        this.updatedAt = Instant.now();
    }

    public void discard(LocalDate fechaFin, UUID updatedByUserId) {
        assertEditable();
        if (fechaFin == null || fechaFin.isBefore(this.fechaInicio)) {
            throw new IllegalArgumentException("La fecha de descarte es invalida");
        }
        this.estado = DiagnosticoClinicoEstado.DESCARTADO;
        this.fechaFin = fechaFin;
        this.updatedByUserId = updatedByUserId;
        this.updatedAt = Instant.now();
    }

    public boolean isEditable() {
        return this.estado == DiagnosticoClinicoEstado.ACTIVO;
    }

    private void assertEditable() {
        if (!isEditable()) {
            throw new HistoriaClinicaConflictException("El diagnostico ya no admite cambios");
        }
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getProfesionalId() { return profesionalId; }
    public UUID getSesionId() { return sesionId; }
    public UUID getCasoAtencionId() { return casoAtencionId; }
    public String getCodigo() { return codigo; }
    public String getDescripcion() { return descripcion; }
    public String getDiagnosticoTipo() { return diagnosticoTipo; }
    public String getDiagnosticoCategoriaCodigo() { return diagnosticoCategoriaCodigo; }
    public String getDiagnosticoCategoriaNombre() { return diagnosticoCategoriaNombre; }
    public String getDiagnosticoSubcategoria() { return diagnosticoSubcategoria; }
    public String getDiagnosticoRegionAnatomica() { return diagnosticoRegionAnatomica; }
    public DiagnosticoClinicoEstado getEstado() { return estado; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public String getNotas() { return notas; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getUpdatedByUserId() { return updatedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
