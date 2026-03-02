package com.akine_api.application.dto.command;

public record LoginCommand(
        String email,
        String password
) {}
