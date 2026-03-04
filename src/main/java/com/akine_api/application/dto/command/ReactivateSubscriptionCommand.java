package com.akine_api.application.dto.command;

import java.util.UUID;

public record ReactivateSubscriptionCommand(
        UUID subscriptionId,
        UUID actorUserId
) {}
