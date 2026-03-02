package com.akine_api.application.dto.result;

import java.util.UUID;

public record ConsultorioDuracionTurnoResult(
        UUID id,
        UUID consultorioId,
        int minutos
) {}
