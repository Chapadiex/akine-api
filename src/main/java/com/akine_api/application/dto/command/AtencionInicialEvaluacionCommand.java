package com.akine_api.application.dto.command;

import java.math.BigDecimal;

public record AtencionInicialEvaluacionCommand(
        BigDecimal peso,
        BigDecimal altura,
        BigDecimal imc,
        String presionArterial,
        Integer frecuenciaCardiaca,
        Integer saturacion,
        BigDecimal temperatura,
        String observaciones
) {
}
