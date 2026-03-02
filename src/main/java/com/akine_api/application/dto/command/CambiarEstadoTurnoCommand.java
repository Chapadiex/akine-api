package com.akine_api.application.dto.command;

import com.akine_api.domain.model.TurnoEstado;

import java.util.UUID;

public record CambiarEstadoTurnoCommand(
        UUID turnoId,
        TurnoEstado nuevoEstado
) {}
