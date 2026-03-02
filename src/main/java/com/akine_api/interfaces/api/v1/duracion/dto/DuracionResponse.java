package com.akine_api.interfaces.api.v1.duracion.dto;

import java.util.UUID;

public record DuracionResponse(
        UUID id,
        UUID consultorioId,
        int minutos
) {}
