package com.akine_api.application.dto.command;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

public record SetHorarioConsultorioCommand(
        UUID consultorioId,
        DayOfWeek diaSemana,
        LocalTime horaApertura,
        LocalTime horaCierre
) {}
