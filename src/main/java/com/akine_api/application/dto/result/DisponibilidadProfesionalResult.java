package com.akine_api.application.dto.result;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record DisponibilidadProfesionalResult(
        UUID id,
        UUID profesionalId,
        UUID consultorioId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        boolean activo
) {}
