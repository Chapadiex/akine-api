package com.akine_api.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReprogramarTurnoCommand(
        UUID turnoId,
        LocalDateTime nuevaFechaHoraInicio
) {}
