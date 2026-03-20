package com.akine_api.interfaces.api.v1.casoatencion.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateCasoAtencionRequest(
        UUID profesionalResponsableId,
        @Size(max = 1000)
        String motivoConsulta,
        @Size(max = 1000)
        String diagnosticoMedico,
        @Size(max = 1000)
        String diagnosticoFuncional,
        @Size(max = 1000)
        String afeccionPrincipal,
        @Size(max = 20)
        String prioridad
) {}
