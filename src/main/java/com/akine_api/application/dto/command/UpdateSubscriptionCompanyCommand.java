package com.akine_api.application.dto.command;

import java.util.UUID;

public record UpdateSubscriptionCompanyCommand(
        UUID subscriptionId,
        String name,
        String cuit,
        String address,
        String city,
        String province
) {}
