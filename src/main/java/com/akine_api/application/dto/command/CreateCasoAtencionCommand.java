package com.akine_api.application.dto.command;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateCasoAtencionCommand(
        UUID legajoId,
        UUID pacienteId,
        UUID profesionalResponsableId,
        String tipoOrigen,
        LocalDateTime fechaApertura,
        String motivoConsulta,
        String diagnosticoMedico,
        String diagnosticoFuncional,
        String afeccionPrincipal,
        UUID coberturaId,
        String prioridad
) {}
