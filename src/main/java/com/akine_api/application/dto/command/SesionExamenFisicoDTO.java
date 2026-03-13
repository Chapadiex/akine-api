package com.akine_api.application.dto.command;

public record SesionExamenFisicoDTO(
        String rangoMovimientoJson,
        String fuerzaMuscularJson,
        String funcionalidadNota,
        String marchaBalanceNota,
        String signosInflamatorios,
        String observacionesNeuroResp,
        String testsMedidasJson
) {}
