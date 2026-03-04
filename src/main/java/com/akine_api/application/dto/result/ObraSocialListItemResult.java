package com.akine_api.application.dto.result;

import com.akine_api.domain.model.ObraSocialEstado;

import java.time.Instant;
import java.util.UUID;

public record ObraSocialListItemResult(
        UUID id,
        UUID consultorioId,
        String acronimo,
        String nombreCompleto,
        String cuit,
        String email,
        String telefono,
        String representante,
        ObraSocialEstado estado,
        long planesCount,
        boolean hasPlanes,
        Instant createdAt,
        Instant updatedAt
) {}

