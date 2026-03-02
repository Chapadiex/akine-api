package com.akine_api.application.dto.command;

public record CreateConsultorioCommand(
        String name,
        String cuit,
        String address,
        String phone,
        String email
) {}
