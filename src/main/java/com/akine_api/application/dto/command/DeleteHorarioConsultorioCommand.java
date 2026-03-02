package com.akine_api.application.dto.command;

import java.time.DayOfWeek;
import java.util.UUID;

public record DeleteHorarioConsultorioCommand(
        UUID consultorioId,
        DayOfWeek diaSemana
) {}
