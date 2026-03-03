package com.akine_api.domain.model;

import com.akine_api.domain.exception.TransicionEstadoInvalidaException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public class Turno {

    private final UUID id;
    private final UUID consultorioId;
    private final UUID profesionalId;
    private UUID boxId;
    private UUID pacienteId;
    private LocalDateTime fechaHoraInicio;
    private int duracionMinutos;
    private TurnoEstado estado;
    private String motivoConsulta;
    private String notas;
    private TipoConsulta tipoConsulta;
    private String telefonoContacto;
    private UUID creadoPorUserId;
    private String motivoCancelacion;
    private UUID canceladoPorUserId;
    private final Instant createdAt;
    private Instant updatedAt;

    public Turno(UUID id, UUID consultorioId, UUID profesionalId, UUID boxId,
                 UUID pacienteId, LocalDateTime fechaHoraInicio, int duracionMinutos,
                 TurnoEstado estado, String motivoConsulta, String notas,
                 TipoConsulta tipoConsulta, String telefonoContacto, UUID creadoPorUserId,
                 String motivoCancelacion, UUID canceladoPorUserId, Instant createdAt) {
        if (fechaHoraInicio == null) {
            throw new IllegalArgumentException("La fecha y hora de inicio es obligatoria");
        }
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración del turno debe ser mayor a 0");
        }
        if (estado == null) {
            throw new IllegalArgumentException("El estado del turno es obligatorio");
        }
        this.id = id;
        this.consultorioId = consultorioId;
        this.profesionalId = profesionalId;
        this.boxId = boxId;
        this.pacienteId = pacienteId;
        this.fechaHoraInicio = fechaHoraInicio;
        this.duracionMinutos = duracionMinutos;
        this.estado = estado;
        this.motivoConsulta = motivoConsulta;
        this.notas = notas;
        this.tipoConsulta = tipoConsulta != null ? tipoConsulta : TipoConsulta.PARTICULAR;
        this.telefonoContacto = telefonoContacto;
        this.creadoPorUserId = creadoPorUserId;
        this.motivoCancelacion = motivoCancelacion;
        this.canceladoPorUserId = canceladoPorUserId;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    /** Constructor de compatibilidad (sin campos nuevos) */
    public Turno(UUID id, UUID consultorioId, UUID profesionalId, UUID boxId,
                 UUID pacienteId, LocalDateTime fechaHoraInicio, int duracionMinutos,
                 TurnoEstado estado, String motivoConsulta, String notas, Instant createdAt) {
        this(id, consultorioId, profesionalId, boxId, pacienteId, fechaHoraInicio,
                duracionMinutos, estado, motivoConsulta, notas,
                TipoConsulta.PARTICULAR, null, null, null, null, createdAt);
    }

    public void reprogramar(LocalDateTime nuevaFechaHoraInicio) {
        if (nuevaFechaHoraInicio == null) {
            throw new IllegalArgumentException("La nueva fecha y hora es obligatoria");
        }
        this.fechaHoraInicio = nuevaFechaHoraInicio;
        this.updatedAt = Instant.now();
    }

    public void cambiarEstado(TurnoEstado nuevoEstado) {
        if (!this.estado.canTransitionTo(nuevoEstado)) {
            throw new TransicionEstadoInvalidaException(
                    "No se puede pasar de " + this.estado + " a " + nuevoEstado);
        }
        this.estado = nuevoEstado;
        this.updatedAt = Instant.now();
    }

    public void cancelar() {
        cambiarEstado(TurnoEstado.CANCELADO);
    }

    public void cancelar(String motivo, UUID canceladoPor) {
        cambiarEstado(TurnoEstado.CANCELADO);
        this.motivoCancelacion = motivo;
        this.canceladoPorUserId = canceladoPor;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraInicio.plusMinutes(duracionMinutos);
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public UUID getProfesionalId() { return profesionalId; }
    public UUID getBoxId() { return boxId; }
    public UUID getPacienteId() { return pacienteId; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public int getDuracionMinutos() { return duracionMinutos; }
    public TurnoEstado getEstado() { return estado; }
    public String getMotivoConsulta() { return motivoConsulta; }
    public String getNotas() { return notas; }
    public TipoConsulta getTipoConsulta() { return tipoConsulta; }
    public String getTelefonoContacto() { return telefonoContacto; }
    public UUID getCreadoPorUserId() { return creadoPorUserId; }
    public String getMotivoCancelacion() { return motivoCancelacion; }
    public UUID getCanceladoPorUserId() { return canceladoPorUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
