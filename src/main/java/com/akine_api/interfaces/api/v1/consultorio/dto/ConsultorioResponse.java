package com.akine_api.interfaces.api.v1.consultorio.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ConsultorioResponse(
        UUID id,
        String name,
        String cuit,
        String address,
        String phone,
        String email,
        BigDecimal mapLatitude,
        BigDecimal mapLongitude,
        String googleMapsUrl,
        String status,
        Instant createdAt,
        Instant updatedAt
) {}
