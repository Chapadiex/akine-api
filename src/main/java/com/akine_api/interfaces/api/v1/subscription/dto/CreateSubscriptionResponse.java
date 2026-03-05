package com.akine_api.interfaces.api.v1.subscription.dto;

import java.util.UUID;

public record CreateSubscriptionResponse(
        UUID subscriptionId,
        String status,
        String message,
        String trackingToken
) {}
