package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateSubscriptionClinicCommand(
        UUID subscriptionId,
        String name,
        String address,
        String phone,
        String email
) {}
