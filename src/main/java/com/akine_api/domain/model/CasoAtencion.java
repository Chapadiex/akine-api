package com.akine_api.domain.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class CasoAtencion {

    private final UUID id;
    private final UUID legajoId;
    private final UUID consultorioId;
    private final UUID pacienteId;
    private UUID profesionalResponsableId;
    private String tipoOrigen;
    private LocalDateTime fechaApertura;
    private String motivoConsulta;
    private String diagnosticoMedico;
    private String diagnosticoFuncional;
    private String afeccionPrincipal;
    private UUID coberturaId;
    private CasoAtencionEstado estado;
    private String prioridad;
    private UUID atencionInicialId;
    private UUID createdByUserId;
    private UUID updatedByUserId;
    private final Instant createdAt;
    private Instant updatedAt;

    public CasoAtencion(UUID id, UUID legajoId, UUID consultorioId, UUID pacienteId,
                        UUID profesionalResponsableId, String tipoOrigen, LocalDateTime fechaApertura,
                        String motivoConsulta, String diagnosticoMedico, String diagnosticoFuncional,
                        String afeccionPrincipal, UUID coberturaId, CasoAtencionEstado estado,
                        String prioridad, UUID atencionInicialId,
                        UUID createdByUserId, UUID updatedByUserId,
                        Instant createdAt, Instant updatedAt) {
        if (legajoId == null) throw new IllegalArgumentException("legajoId es obligatorio");
        if (consultorioId == null) throw new IllegalArgumentException("consultorioId es obligatorio");
        if (pacienteId == null) throw new IllegalArgumentException("pacienteId es obligatorio");
        if (estado == null) throw new IllegalArgumentException("estado es obligatorio");
        this.id = id;
        this.legajoId = legajoId;
        this.consultorioId = consultorioId;
        this.pacienteId = pacienteId;
        this.profesionalResponsableId = profesionalResponsableId;
        this.tipoOrigen = tipoOrigen != null ? tipoOrigen : "CONSULTA_DIRECTA";
        this.fechaApertura = fechaApertura != null ? fechaApertura : LocalDateTime.now();
        this.motivoConsulta = motivoConsulta;
        this.diagnosticoMedico = diagnosticoMedico;
        this.diagnosticoFuncional = diagnosticoFuncional;
        this.afeccionPrincipal = afeccionPrincipal;
        this.coberturaId = coberturaId;
        this.estado = estado;
        this.prioridad = prioridad != null ? prioridad : "NORMAL";
        this.atencionInicialId = atencionInicialId;
        this.createdByUserId = createdByUserId;
        this.updatedByUserId = updatedByUserId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt != null ? updatedAt : createdAt;
    }

    public void cambiarEstado(CasoAtencionEstado nuevoEstado, UUID updatedBy) {
        this.estado.validateTransition(nuevoEstado);
        this.estado = nuevoEstado;
        this.updatedByUserId = updatedBy;
        this.updatedAt = Instant.now();
    }

    public void update(String motivoConsulta, String diagnosticoMedico, String diagnosticoFuncional,
                       String afeccionPrincipal, String prioridad, UUID profesionalResponsableId,
                       UUID updatedBy) {
        this.motivoConsulta = motivoConsulta;
        this.diagnosticoMedico = diagnosticoMedico;
        this.diagnosticoFuncional = diagnosticoFuncional;
        this.afeccionPrincipal = afeccionPrincipal;
        if (prioridad != null) this.prioridad = prioridad;
        if (profesionalResponsableId != null) this.profesionalResponsableId = profesionalResponsableId;
        this.updatedByUserId = updatedBy;
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getLegajoId() { return legajoId; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getPacienteId() { return pacienteId; }
    public UUID getProfesionalResponsableId() { return profesionalResponsableId; }
    public String getTipoOrigen() { return tipoOrigen; }
    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getDiagnosticoMedico() { return diagnosticoMedico; }
    public String getDiagnosticoFuncional() { return diagnosticoFuncional; }
    public String getAfeccionPrincipal() { return afeccionPrincipal; }
    public UUID getCoberturaId() { return coberturaId; }
    public CasoAtencionEstado getEstado() { return estado; }
    public String getPrioridad() { return prioridad; }
    public UUID getAtencionInicialId() { return atencionInicialId; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getUpdatedByUserId() { return updatedByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
