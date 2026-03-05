package com.akine_api.application.dto.command;

public record CreateSubscriptionDraftCommand(
        String planCode,
        String billingCycle,
        String ownerEmail,
        String ownerPassword
) {}
