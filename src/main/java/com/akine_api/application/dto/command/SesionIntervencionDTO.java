package com.akine_api.application.dto.command;

import java.util.UUID;

public record SesionIntervencionDTO(
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
