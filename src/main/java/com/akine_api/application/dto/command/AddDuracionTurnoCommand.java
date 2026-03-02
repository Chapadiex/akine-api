package com.akine_api.application.dto.command;

import java.util.UUID;

public record AddDuracionTurnoCommand(
        UUID consultorioId,
        int minutos
) {}
