package com.akine_api.interfaces.api.v1.casoatencion.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateCasoAtencionRequest(
        UUID pacienteId,
        UUID profesionalResponsableId,
        @Size(max = 40)
        String tipoOrigen,
        LocalDateTime fechaApertura,
        @Size(max = 500)
        String motivoConsulta,
        @Size(max = 500)
        String diagnosticoMedico,
        @Size(max = 500)
        String diagnosticoFuncional,
        @Size(max = 255)
        String afeccionPrincipal,
        UUID coberturaId,
        @Size(max = 20)
        String prioridad
) {}
