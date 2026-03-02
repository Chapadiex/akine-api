package com.akine_api.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class DisponibilidadProfesional {

    private final UUID id;
    private UUID profesionalId;
    private UUID consultorioId;
    private DayOfWeek diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean activo;

    public DisponibilidadProfesional(UUID id, UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana,
                                     LocalTime horaInicio, LocalTime horaFin, boolean activo) {
        validateRange(horaInicio, horaFin);
        this.id = id;
        this.profesionalId = profesionalId;
        this.consultorioId = consultorioId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.activo = activo;
    }

    public void update(UUID profesionalId, UUID consultorioId, DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        validateRange(horaInicio, horaFin);
        this.profesionalId = profesionalId;
        this.consultorioId = consultorioId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.activo = true;
    }

    private void validateRange(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null || !inicio.isBefore(fin)) {
            throw new IllegalArgumentException("La disponibilidad profesional es invalida");
        }
    }

    public UUID getId() { return id; }
    public UUID getProfesionalId() { return profesionalId; }
    public UUID getConsultorioId() { return consultorioId; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFin() { return horaFin; }
    public boolean isActivo() { return activo; }
}
