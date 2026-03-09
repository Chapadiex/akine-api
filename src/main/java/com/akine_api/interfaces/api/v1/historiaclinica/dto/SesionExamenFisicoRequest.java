package com.akine_api.interfaces.api.v1.historiaclinica.dto;

public record SesionExamenFisicoRequest(
        String rangoMovimientoJson,
        String fuerzaMuscularJson,
        String funcionalidadNota,
        String marchaBalanceNota,
        String signosInflamatorios,
        String observacionesNeuroResp,
        String testsMedidasJson
) {}
