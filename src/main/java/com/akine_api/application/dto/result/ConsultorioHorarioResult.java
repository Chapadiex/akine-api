package com.akine_api.application.dto.result;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record ConsultorioHorarioResult(
        UUID id,
        UUID consultorioId,
        DayOfWeek diaSemana,
        LocalTime horaApertura,
        LocalTime horaCierre,
        boolean activo
) {}
