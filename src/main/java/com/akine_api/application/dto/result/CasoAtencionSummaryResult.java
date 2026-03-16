package com.akine_api.application.dto.result;

import com.akine_api.domain.model.CasoAtencionEstado;

import java.time.LocalDateTime;
import java.util.UUID;

public record CasoAtencionSummaryResult(
        UUID id,
        UUID legajoId,
        UUID pacienteId,
        UUID profesionalResponsableId,
        String profesionalResponsableNombre,
        String tipoOrigen,
        LocalDateTime fechaApertura,
        String motivoConsulta,
        String diagnosticoMedico,
        String afeccionPrincipal,
        CasoAtencionEstado estado,
        String prioridad,
        int cantidadSesiones,
        int cantidadPlanes
) {}
