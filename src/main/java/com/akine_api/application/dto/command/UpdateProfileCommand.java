package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateProfileCommand(
        UUID userId,
        String firstName,
        String lastName,
        String phone
) {}
