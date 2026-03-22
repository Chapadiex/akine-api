package com.akine_api.domain.model;

import com.akine_api.domain.exception.HistoriaClinicaConflictException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class SesionClinica {

    private final UUID id;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private final HistoriaClinicaOrigenRegistro origenRegistro;
    private final UUID createdByUserId;
    private final Instant createdAt;
    private UUID profesionalId;
    private UUID turnoId;
    private UUID casoAtencionId;
    private UUID boxId;
    private LocalDateTime fechaAtencion;
    private HistoriaClinicaSesionEstado estado;
    private HistoriaClinicaTipoAtencion tipoAtencion;
    private String motivoConsulta;
    private String resumenClinico;
    private String subjetivo;
    private String objetivo;
    private String evaluacion;
    private String plan;
    private UUID updatedByUserId;
    private UUID closedByUserId;
    private Instant updatedAt;
    private Instant closedAt;
    // Fase 2 — cierre clínico
    private Integer duracionRealMinutos;
    private String tratamientoRealizado;
    private String resultadoClinico;
    private String conductaSiguiente;
    private boolean requiereSeguimiento;
    private String observacionesClincias;
    private boolean cerradaClinicamente;
    private Instant fechaCierreClinco;
    private UUID cierreClinicoPor;
    private boolean esGrupal;
    private UUID grupoSesionId;

    public SesionClinica(UUID id,
                         UUID consultorioId,
                         UUID pacienteId,
                         UUID profesionalId,
                         UUID turnoId,
                         UUID casoAtencionId,
                         UUID boxId,
                         LocalDateTime fechaAtencion,
                         HistoriaClinicaSesionEstado estado,
                         HistoriaClinicaTipoAtencion tipoAtencion,
                         String motivoConsulta,
                         String resumenClinico,
                         String subjetivo,
                         String objetivo,
                         String evaluacion,
                         String plan,
                         HistoriaClinicaOrigenRegistro origenRegistro,
                         UUID createdByUserId,
                         UUID updatedByUserId,
                         UUID closedByUserId,
                         Instant createdAt,
                         Instant updatedAt,
                         Instant closedAt) {
        if (consultorioId == null || pacienteId == null) {
            throw new IllegalArgumentException("Consultorio y paciente son obligatorios");
        }
        if (fechaAtencion == null || estado == null || tipoAtencion == null || origenRegistro == null) {
            throw new IllegalArgumentException("La sesion clinica requiere fecha, estado, tipo y origen");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.profesionalId = profesionalId;
        this.turnoId = turnoId;
        this.casoAtencionId = casoAtencionId;
        this.boxId = boxId;
        this.fechaAtencion = fechaAtencion;
        this.estado = estado;
        this.tipoAtencion = tipoAtencion;
        this.motivoConsulta = motivoConsulta;
        this.resumenClinico = resumenClinico;
        this.subjetivo = subjetivo;
        this.objetivo = objetivo;
        this.evaluacion = evaluacion;
        this.plan = plan;
        this.origenRegistro = origenRegistro;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.closedByUserId = closedByUserId;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
        this.updatedAt = updatedAt != null ? updatedAt : this.createdAt;
        this.closedAt = closedAt;
    }

    public void update(UUID profesionalId,
                       UUID turnoId,
                       UUID boxId,
                       LocalDateTime fechaAtencion,
                       HistoriaClinicaTipoAtencion tipoAtencion,
                       String motivoConsulta,
                       String resumenClinico,
                       String subjetivo,
                       String objetivo,
                       String evaluacion,
                       String plan,
                       UUID updatedByUserId) {
        assertEditable();
        if (fechaAtencion == null || tipoAtencion == null) {
            throw new IllegalArgumentException("Fecha y tipo de atencion son obligatorios");
        }
        this.profesionalId = profesionalId;
        this.turnoId = turnoId;
        this.boxId = boxId;
        this.fechaAtencion = fechaAtencion;
        this.tipoAtencion = tipoAtencion;
        this.motivoConsulta = motivoConsulta;
        this.resumenClinico = resumenClinico;
        this.subjetivo = subjetivo;
        this.objetivo = objetivo;
        this.evaluacion = evaluacion;
        this.plan = plan;
        this.updatedByUserId = updatedByUserId;
        this.updatedAt = Instant.now();
    }

    public void close(UUID actorUserId) {
        assertEditable();
        this.estado = HistoriaClinicaSesionEstado.CERRADA;
        this.updatedByUserId = actorUserId;
        this.closedByUserId = actorUserId;
        this.updatedAt = Instant.now();
        this.closedAt = this.updatedAt;
    }

    public void annul(UUID actorUserId) {
        if (this.estado == HistoriaClinicaSesionEstado.ANULADA) {
            throw new HistoriaClinicaConflictException("La sesion ya se encuentra anulada");
        }
        this.estado = HistoriaClinicaSesionEstado.ANULADA;
        this.updatedByUserId = actorUserId;
        this.closedByUserId = actorUserId;
        this.updatedAt = Instant.now();
        this.closedAt = this.updatedAt;
    }

    /**
     * Updates the editable clinical fields (tratamiento, resultado, etc.).
     * Must be called while the session is still BORRADOR.
     */
    public void updateCamposClinicos(Integer duracionRealMinutos,
                                      String tratamientoRealizado,
                                      String resultadoClinico,
                                      String conductaSiguiente,
                                      boolean requiereSeguimiento,
                                      String observacionesClincias,
                                      boolean esGrupal,
                                      UUID grupoSesionId,
                                      UUID updatedByUserId) {
        assertEditable();
        this.duracionRealMinutos = duracionRealMinutos;
        this.tratamientoRealizado = tratamientoRealizado;
        this.resultadoClinico = resultadoClinico;
        this.conductaSiguiente = conductaSiguiente;
        this.requiereSeguimiento = requiereSeguimiento;
        this.observacionesClincias = observacionesClincias;
        this.esGrupal = esGrupal;
        this.grupoSesionId = grupoSesionId;
        this.updatedByUserId = updatedByUserId;
        this.updatedAt = Instant.now();
    }

    /**
     * Marks the session as clinically closed. Requires 4 mandatory fields:
     * tratamientoRealizado, resultadoClinico, duracionRealMinutos, conductaSiguiente.
     */
    public void cerrarClinicamente(UUID actorUserId) {
        assertEditable();
        if (tratamientoRealizado == null || tratamientoRealizado.isBlank()) {
            throw new HistoriaClinicaConflictException("El tratamiento realizado es obligatorio para el cierre clínico");
        }
        if (resultadoClinico == null || resultadoClinico.isBlank()) {
            throw new HistoriaClinicaConflictException("El resultado clínico es obligatorio para el cierre clínico");
        }
        if (duracionRealMinutos == null || duracionRealMinutos <= 0) {
            throw new HistoriaClinicaConflictException("La duración real en minutos es obligatoria para el cierre clínico");
        }
        if (conductaSiguiente == null || conductaSiguiente.isBlank()) {
            throw new HistoriaClinicaConflictException("La conducta siguiente es obligatoria para el cierre clínico");
        }
        this.cerradaClinicamente = true;
        this.fechaCierreClinco = Instant.now();
        this.cierreClinicoPor = actorUserId;
        this.estado = HistoriaClinicaSesionEstado.CERRADA;
        this.closedByUserId = actorUserId;
        this.updatedByUserId = actorUserId;
        this.updatedAt = this.fechaCierreClinco;
        this.closedAt = this.fechaCierreClinco;
    }

    // Setters for loading from persistence (mapper use only)
    public void setDuracionRealMinutos(Integer v) { this.duracionRealMinutos = v; }
    public void setTratamientoRealizado(String v) { this.tratamientoRealizado = v; }
    public void setResultadoClinico(String v) { this.resultadoClinico = v; }
    public void setConductaSiguiente(String v) { this.conductaSiguiente = v; }
    public void setRequiereSeguimiento(boolean v) { this.requiereSeguimiento = v; }
    public void setObservacionesClincias(String v) { this.observacionesClincias = v; }
    public void setCerradaClinicamente(boolean v) { this.cerradaClinicamente = v; }
    public void setFechaCierreClinco(Instant v) { this.fechaCierreClinco = v; }
    public void setCierreClinicoPor(UUID v) { this.cierreClinicoPor = v; }
    public void setEsGrupal(boolean v) { this.esGrupal = v; }
    public void setGrupoSesionId(UUID v) { this.grupoSesionId = v; }

    public boolean isEditable() {
        return this.estado == HistoriaClinicaSesionEstado.BORRADOR;
    }

    private void assertEditable() {
        if (!isEditable()) {
            throw new HistoriaClinicaConflictException("La sesion clinica ya no admite edicion");
        }
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getProfesionalId() { return profesionalId; }
    public UUID getTurnoId() { return turnoId; }
    public UUID getCasoAtencionId() { return casoAtencionId; }
    public UUID getBoxId() { return boxId; }
    public LocalDateTime getFechaAtencion() { return fechaAtencion; }
    public HistoriaClinicaSesionEstado getEstado() { return estado; }
    public HistoriaClinicaTipoAtencion getTipoAtencion() { return tipoAtencion; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getResumenClinico() { return resumenClinico; }
    public String getSubjetivo() { return subjetivo; }
    public String getObjetivo() { return objetivo; }
    public String getEvaluacion() { return evaluacion; }
    public String getPlan() { return plan; }
    public HistoriaClinicaOrigenRegistro getOrigenRegistro() { return origenRegistro; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getUpdatedByUserId() { return updatedByUserId; }
    public UUID getClosedByUserId() { return closedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getClosedAt() { return closedAt; }
    public Integer getDuracionRealMinutos() { return duracionRealMinutos; }
    public String getTratamientoRealizado() { return tratamientoRealizado; }
    public String getResultadoClinico() { return resultadoClinico; }
    public String getConductaSiguiente() { return conductaSiguiente; }
    public boolean isRequiereSeguimiento() { return requiereSeguimiento; }
    public String getObservacionesClincias() { return observacionesClincias; }
    public boolean isCerradaClinicamente() { return cerradaClinicamente; }
    public Instant getFechaCierreClinco() { return fechaCierreClinco; }
    public UUID getCierreClinicoPor() { return cierreClinicoPor; }
    public boolean isEsGrupal() { return esGrupal; }
    public UUID getGrupoSesionId() { return grupoSesionId; }
}
