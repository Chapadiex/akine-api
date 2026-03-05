package com.akine_api.application.dto.command;

import java.util.UUID;

public record SimulateSubscriptionPaymentCommand(
        UUID subscriptionId,
        String paymentReference
) {}
