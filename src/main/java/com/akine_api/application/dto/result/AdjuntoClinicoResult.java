package com.akine_api.application.dto.result;

import java.time.Instant;
import java.util.UUID;

public record AdjuntoClinicoResult(
        UUID id,
        UUID sesionId,
        UUID atencionInicialId,
        UUID casoAtencionId,
        String originalFilename,
        String contentType,
        long sizeBytes,
        Instant createdAt
) {}
