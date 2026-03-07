package com.akine_api.application.dto.command;

import java.util.UUID;

public record ChangeSesionClinicaEstadoCommand(
        UUID consultorioId,
        UUID pacienteId,
        UUID sesionId,
        UUID actorUserId
) {}
