package com.akine_api.interfaces.api.v1.historiaclinica.dto;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtencionInicialEvaluacionRequest(
        BigDecimal peso,
        BigDecimal altura,
        BigDecimal imc,
        @Size(max = 30) String presionArterial,
        Integer frecuenciaCardiaca,
        Integer saturacion,
        BigDecimal temperatura,
        @Size(max = 1000) String observaciones
) {
}
