package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record CreateDiagnosticoClinicoCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalId,
        UUID sesionId,
        String diagnosticoCodigo,
        LocalDate fechaInicio,
        String notas,
        UUID actorUserId
) {}
