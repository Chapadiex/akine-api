package com.akine_api.application.dto.command;

import java.math.BigDecimal;

public record CreateConsultorioCommand(
        String name,
        String cuit,
        String address,
        String phone,
        String email,
        BigDecimal mapLatitude,
        BigDecimal mapLongitude,
        String googleMapsUrl
) {}
