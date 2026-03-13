package com.akine_api.interfaces.api.v1.historiaclinica.dto;

public record SesionEvaluacionRequest(
        Integer dolorIntensidad,
        String dolorZona,
        String dolorLateralidad,
        String dolorTipo,
        String dolorComportamiento,
        String evolucionEstado,
        String evolucionNota,
        String objetivoSesion,
        String limitacionFuncional,
        String respuestaPaciente,
        String tolerancia,
        String indicacionesDomiciliarias,
        String proximaConducta
) {}
