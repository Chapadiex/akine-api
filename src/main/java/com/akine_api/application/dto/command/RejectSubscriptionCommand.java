package com.akine_api.application.dto.command;

import java.util.UUID;

public record RejectSubscriptionCommand(
        UUID subscriptionId,
        UUID actorUserId,
        String reason
) {}
