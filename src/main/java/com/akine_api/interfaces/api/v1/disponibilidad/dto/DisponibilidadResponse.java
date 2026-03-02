package com.akine_api.interfaces.api.v1.disponibilidad.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record DisponibilidadResponse(
        UUID id,
        UUID profesionalId,
        UUID consultorioId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        boolean activo
) {}
