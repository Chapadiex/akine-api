package com.akine_api.interfaces.api.v1.turno.dto;

import java.time.LocalDateTime;

public record SlotDisponibleResponse(
        LocalDateTime inicio,
        LocalDateTime fin
) {}
