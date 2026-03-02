package com.akine_api.application.dto.command;

import java.util.UUID;

public record AsignarProfesionalCommand(
        UUID profesionalId,
        UUID consultorioId
) {}
