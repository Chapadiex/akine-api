package com.akine_api.application.dto.result;

import java.util.UUID;

public record BoxDisponibilidadResult(
        UUID id,
        String nombre,
        boolean disponible,
        Integer capacidadTotal,
        Integer capacidadUsada
) {}
