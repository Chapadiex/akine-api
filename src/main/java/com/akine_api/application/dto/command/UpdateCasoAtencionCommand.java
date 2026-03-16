package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateCasoAtencionCommand(
        UUID profesionalResponsableId,
        String motivoConsulta,
        String diagnosticoMedico,
        String diagnosticoFuncional,
        String afeccionPrincipal,
        String prioridad
) {}
