package com.akine_api.interfaces.api.v1.casoatencion.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateCasoAtencionRequest(
        UUID profesionalResponsableId,
        @Size(max = 500)
        String motivoConsulta,
        @Size(max = 500)
        String diagnosticoMedico,
        @Size(max = 500)
        String diagnosticoFuncional,
        @Size(max = 255)
        String afeccionPrincipal,
        @Size(max = 20)
        String prioridad
) {}
