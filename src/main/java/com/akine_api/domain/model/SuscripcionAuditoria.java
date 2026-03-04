package com.akine_api.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SuscripcionAuditoria(
        UUID id,
        UUID suscripcionId,
        String action,
        String fromStatus,
        String toStatus,
        UUID actorUserId,
        String reason,
        Instant createdAt
) {}
