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
        @Size(max = 1000)
        String motivoConsulta,
        @Size(max = 1000)
        String diagnosticoMedico,
        @Size(max = 1000)
        String diagnosticoFuncional,
        @Size(max = 1000)
        String afeccionPrincipal,
        UUID coberturaId,
        @Size(max = 20)
        String prioridad
) {}
