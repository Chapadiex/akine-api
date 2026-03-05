package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateSubscriptionOwnerCommand(
        UUID subscriptionId,
        String firstName,
        String lastName,
        String documentoFiscal,
        String email,
        String phone,
        String password
) {}
