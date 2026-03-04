package com.akine_api.interfaces.api.v1.obrasocial.dto;

import com.akine_api.domain.model.ObraSocialEstado;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ObraSocialDetailResponse(
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
        List<PlanResponse> planes,
        Instant createdAt,
        Instant updatedAt
) {}

