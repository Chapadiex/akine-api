package com.akine_api.application.dto.command;

import java.util.UUID;

public record DeleteDisponibilidadCommand(
        UUID id,
        UUID profesionalId,
        UUID consultorioId
) {}
