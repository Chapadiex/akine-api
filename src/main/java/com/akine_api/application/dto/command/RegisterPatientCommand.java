package com.akine_api.application.dto.command;

public record RegisterPatientCommand(
        String email,
        String password,
        String firstName,
        String lastName,
        String phone
) {}
