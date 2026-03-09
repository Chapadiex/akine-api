package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class AtencionInicial {

    private final UUID id;
    private final UUID legajoId;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final UUID profesionalId;
    private final LocalDateTime fechaHora;
    private final AtencionInicialTipoIngreso tipoIngreso;
    private final String motivoConsultaBreve;
    private final String sintomasPrincipales;
    private final String tiempoEvolucion;
    private final String observaciones;
    private final String especialidadDerivante;
    private final String profesionalDerivante;
    private final LocalDate fechaPrescripcion;
    private final String diagnosticoCodigo;
    private final String diagnosticoNombre;
    private final String diagnosticoTipo;
    private final String diagnosticoCategoriaCodigo;
    private final String diagnosticoCategoriaNombre;
    private final String diagnosticoSubcategoria;
    private final String diagnosticoRegionAnatomica;
    private final String diagnosticoObservacion;
    private final String observacionesPrescripcion;
    private final String resumenClinicoInicial;
    private final String hallazgosRelevantes;
    private final UUID createdByUserId;
    private final UUID updatedByUserId;
    private final Instant createdAt;
    private final Instant updatedAt;

    public AtencionInicial(UUID id,
                           UUID legajoId,
                           UUID consultorioId,
                           UUID pacienteId,
                           UUID profesionalId,
                           LocalDateTime fechaHora,
                           AtencionInicialTipoIngreso tipoIngreso,
                           String motivoConsultaBreve,
                           String sintomasPrincipales,
                           String tiempoEvolucion,
                           String observaciones,
                           String especialidadDerivante,
                           String profesionalDerivante,
                           LocalDate fechaPrescripcion,
                           String diagnosticoCodigo,
                           String diagnosticoNombre,
                           String diagnosticoTipo,
                           String diagnosticoCategoriaCodigo,
                           String diagnosticoCategoriaNombre,
                           String diagnosticoSubcategoria,
                           String diagnosticoRegionAnatomica,
                           String diagnosticoObservacion,
                           String observacionesPrescripcion,
                           String resumenClinicoInicial,
                           String hallazgosRelevantes,
                           UUID createdByUserId,
                           UUID updatedByUserId,
                           Instant createdAt,
                           Instant updatedAt) {
        if (id == null || legajoId == null || consultorioId == null || pacienteId == null || profesionalId == null) {
            throw new IllegalArgumentException("La atencion inicial requiere ids obligatorios");
        }
        if (fechaHora == null || tipoIngreso == null) {
            throw new IllegalArgumentException("La atencion inicial requiere fecha y tipo de ingreso");
        }
        this.id = id;
        this.legajoId = legajoId;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.profesionalId = profesionalId;
        this.fechaHora = fechaHora;
        this.tipoIngreso = tipoIngreso;
        this.motivoConsultaBreve = motivoConsultaBreve;
        this.sintomasPrincipales = sintomasPrincipales;
        this.tiempoEvolucion = tiempoEvolucion;
        this.observaciones = observaciones;
        this.especialidadDerivante = especialidadDerivante;
        this.profesionalDerivante = profesionalDerivante;
        this.fechaPrescripcion = fechaPrescripcion;
        this.diagnosticoCodigo = diagnosticoCodigo;
        this.diagnosticoNombre = diagnosticoNombre;
        this.diagnosticoTipo = diagnosticoTipo;
        this.diagnosticoCategoriaCodigo = diagnosticoCategoriaCodigo;
        this.diagnosticoCategoriaNombre = diagnosticoCategoriaNombre;
        this.diagnosticoSubcategoria = diagnosticoSubcategoria;
        this.diagnosticoRegionAnatomica = diagnosticoRegionAnatomica;
        this.diagnosticoObservacion = diagnosticoObservacion;
        this.observacionesPrescripcion = observacionesPrescripcion;
        this.resumenClinicoInicial = resumenClinicoInicial;
        this.hallazgosRelevantes = hallazgosRelevantes;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
    }

    public UUID getId() { return id; }
    public UUID getLegajoId() { return legajoId; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getProfesionalId() { return profesionalId; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public AtencionInicialTipoIngreso getTipoIngreso() { return tipoIngreso; }
    public String getMotivoConsultaBreve() { return motivoConsultaBreve; }
    public String getSintomasPrincipales() { return sintomasPrincipales; }
    public String getTiempoEvolucion() { return tiempoEvolucion; }
    public String getObservaciones() { return observaciones; }
    public String getEspecialidadDerivante() { return especialidadDerivante; }
    public String getProfesionalDerivante() { return profesionalDerivante; }
    public LocalDate getFechaPrescripcion() { return fechaPrescripcion; }
    public String getDiagnosticoCodigo() { return diagnosticoCodigo; }
    public String getDiagnosticoNombre() { return diagnosticoNombre; }
    public String getDiagnosticoTipo() { return diagnosticoTipo; }
    public String getDiagnosticoCategoriaCodigo() { return diagnosticoCategoriaCodigo; }
    public String getDiagnosticoCategoriaNombre() { return diagnosticoCategoriaNombre; }
    public String getDiagnosticoSubcategoria() { return diagnosticoSubcategoria; }
    public String getDiagnosticoRegionAnatomica() { return diagnosticoRegionAnatomica; }
    public String getDiagnosticoObservacion() { return diagnosticoObservacion; }
    public String getObservacionesPrescripcion() { return observacionesPrescripcion; }
    public String getResumenClinicoInicial() { return resumenClinicoInicial; }
    public String getHallazgosRelevantes() { return hallazgosRelevantes; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getUpdatedByUserId() { return updatedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
