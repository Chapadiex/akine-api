package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record ChangeColaboradorEstadoCommand(
        UUID consultorioId,
        UUID colaboradorId,
        boolean activo,
        LocalDate fechaDeBaja,
        String motivoDeBaja
) {}
