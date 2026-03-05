package com.akine_api.application.dto.command;

import java.util.UUID;

public record RequestSubscriptionInfoCommand(
        UUID subscriptionId,
        UUID actorUserId,
        String reason
) {}
