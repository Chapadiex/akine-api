package com.akine_api.application.dto.command;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateDiagnosticoClinicoCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID diagnosticoId,
        UUID profesionalId,
        UUID sesionId,
        String diagnosticoCodigo,
        LocalDate fechaInicio,
        String notas,
        UUID actorUserId
) {}
