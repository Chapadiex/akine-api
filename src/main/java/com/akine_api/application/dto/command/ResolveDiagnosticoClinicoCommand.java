package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record ResolveDiagnosticoClinicoCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID diagnosticoId,
        LocalDate fechaFin,
        UUID actorUserId
) {}
