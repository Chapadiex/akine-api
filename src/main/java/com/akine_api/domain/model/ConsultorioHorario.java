package com.akine_api.domain.model;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public class ConsultorioHorario {

    private final UUID id;
    private final UUID consultorioId;
    private DayOfWeek diaSemana;
    private LocalTime horaApertura;
    private LocalTime horaCierre;
    private boolean activo;

    public ConsultorioHorario(UUID id, UUID consultorioId, DayOfWeek diaSemana,
                              LocalTime horaApertura, LocalTime horaCierre, boolean activo) {
        validateRange(horaApertura, horaCierre);
        this.id = id;
        this.consultorioId = consultorioId;
        this.diaSemana = diaSemana;
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.activo = activo;
    }

    public void update(LocalTime horaApertura, LocalTime horaCierre) {
        validateRange(horaApertura, horaCierre);
        this.horaApertura = horaApertura;
        this.horaCierre = horaCierre;
        this.activo = true;
    }

    private void validateRange(LocalTime apertura, LocalTime cierre) {
        if (apertura == null || cierre == null || !apertura.isBefore(cierre)) {
            throw new IllegalArgumentException("El horario del consultorio es invalido");
        }
    }

    public UUID getId() { return id; }
    public UUID getConsultorioId() { return consultorioId; }
    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getHoraApertura() { return horaApertura; }
    public LocalTime getHoraCierre() { return horaCierre; }
    public boolean isActivo() { return activo; }
}
