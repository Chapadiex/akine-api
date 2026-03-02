package com.akine_api.interfaces.api.v1.disponibilidad.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadRequest(
        @NotNull(message = "El dia de semana es obligatorio")
        DayOfWeek diaSemana,
        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,
        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin
) {}
