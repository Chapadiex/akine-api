package com.akine_api.application.dto.result;

import java.math.BigDecimal;
import java.util.UUID;

public record AtencionInicialEvaluacionResult(
        UUID id,
        UUID atencionInicialId,
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
