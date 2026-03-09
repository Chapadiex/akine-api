package com.akine_api.application.dto.command;

public record SesionEvaluacionDTO(
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
