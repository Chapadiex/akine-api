package com.akine_api.application.dto.command;

import java.util.List;
import java.util.UUID;

public record UpdateHistoriaClinicaAntecedentesCommand(
        UUID consultorioId,
        UUID pacienteId,
        List<HistoriaClinicaAntecedenteItemCommand> antecedentes,
        UUID actorUserId
) {}
