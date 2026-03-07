package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record AdjuntoClinicoResult(
        UUID id,
        UUID sesionId,
        String originalFilename,
        String contentType,
        long sizeBytes,
        Instant createdAt
) {}
