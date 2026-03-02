package com.akine_api.interfaces.api.v1.horario.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record HorarioResponse(
        UUID id,
        UUID consultorioId,
        DayOfWeek diaSemana,
        LocalTime horaApertura,
        LocalTime horaCierre,
        boolean activo
) {}
