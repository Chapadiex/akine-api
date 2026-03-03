package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateFeriadoCommand(
        UUID consultorioId,
        LocalDate fecha,
        String descripcion
) {}
