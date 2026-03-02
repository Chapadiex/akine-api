package com.akine_api.application.dto.command;

import java.util.UUID;

public record RemoveDuracionTurnoCommand(
        UUID consultorioId,
        int minutos
) {}
