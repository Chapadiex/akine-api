package com.akine_api.interfaces.api.v1.admin.dto;

import java.time.Instant;
import java.util.UUID;

public record SubscriptionAuditItemResponse(
        UUID id,
        String action,
        String fromStatus,
        String toStatus,
        UUID actorUserId,
        String reason,
        Instant createdAt
) {}
