package com.akine_api.application.dto.command;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record UpdateDisponibilidadCommand(
        UUID id,
        UUID profesionalId,
        UUID consultorioId,
        DayOfWeek diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin
) {}
