package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import java.util.UUID;

public record SesionIntervencionRequest(
        String tratamientoId,
        String tratamientoNombre,
        String técnica,
        String zona,
        String parametrosJson,
        Integer duracionMinutos,
        UUID profesionalId,
        String observaciones,
        int orderIndex
) {}
