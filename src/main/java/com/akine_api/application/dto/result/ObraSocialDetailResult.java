package com.akine_api.application.dto.result;

import com.akine_api.domain.model.ObraSocialEstado;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ObraSocialDetailResult(
        UUID id,
        UUID consultorioId,
        String acronimo,
        String nombreCompleto,
        String cuit,
        String email,
        String telefono,
        String telefonoAlternativo,
        String representante,
        String observacionesInternas,
        String direccionLinea,
        ObraSocialEstado estado,
        List<PlanResult> planes,
        Instant createdAt,
        Instant updatedAt
) {}

