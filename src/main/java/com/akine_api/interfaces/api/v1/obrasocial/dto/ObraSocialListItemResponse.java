package com.akine_api.interfaces.api.v1.obrasocial.dto;

import com.akine_api.domain.model.ObraSocialEstado;

import java.time.Instant;
import java.util.UUID;

public record ObraSocialListItemResponse(
        UUID id,
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

