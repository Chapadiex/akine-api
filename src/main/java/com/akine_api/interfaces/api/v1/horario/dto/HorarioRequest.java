package com.akine_api.interfaces.api.v1.horario.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record HorarioRequest(
        @NotNull(message = "El dia de semana es obligatorio")
        DayOfWeek diaSemana,
        @NotNull(message = "La hora de apertura es obligatoria")
        LocalTime horaApertura,
        @NotNull(message = "La hora de cierre es obligatoria")
        LocalTime horaCierre
) {}
