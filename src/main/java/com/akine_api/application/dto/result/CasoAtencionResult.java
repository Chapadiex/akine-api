package com.akine_api.application.dto.result;

import com.akine_api.domain.model.CasoAtencionEstado;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record CasoAtencionResult(
        UUID id,
        UUID legajoId,
        UUID consultorioId,
        UUID pacienteId,
        UUID profesionalResponsableId,
        String profesionalResponsableNombre,
        String tipoOrigen,
        LocalDateTime fechaApertura,
        String motivoConsulta,
        String diagnosticoMedico,
        String diagnosticoFuncional,
        String afeccionPrincipal,
        UUID coberturaId,
        CasoAtencionEstado estado,
        String prioridad,
        UUID atencionInicialId,
        int cantidadSesiones,
        int cantidadPlanes,
        Instant createdAt,
        Instant updatedAt
) {}
