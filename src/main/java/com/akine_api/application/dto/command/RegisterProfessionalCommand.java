package com.akine_api.application.dto.command;

public record RegisterProfessionalCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone,
        String consultorioName,
        String consultorioAddress,
        String consultorioPhone
) {}
