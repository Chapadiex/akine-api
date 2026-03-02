package com.akine_api.application.dto.result;

import java.time.LocalDateTime;

public record SlotDisponibleResult(
        LocalDateTime inicio,
        LocalDateTime fin
) {}
