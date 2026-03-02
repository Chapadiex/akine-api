package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateConsultorioCommand(
        UUID id,
        String name,
        String cuit,
        String address,
        String phone,
        String email
) {}
