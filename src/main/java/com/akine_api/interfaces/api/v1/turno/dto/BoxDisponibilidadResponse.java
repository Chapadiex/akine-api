package com.akine_api.interfaces.api.v1.turno.dto;

public record BoxDisponibilidadResponse(
        String id,
        String nombre,
        boolean disponible,
        Integer capacidadTotal,
        Integer capacidadUsada
) {}
