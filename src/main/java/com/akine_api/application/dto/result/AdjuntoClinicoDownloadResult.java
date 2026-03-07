package com.akine_api.application.dto.result;

public record AdjuntoClinicoDownloadResult(
        String originalFilename,
        String contentType,
        long sizeBytes,
        byte[] content
) {}
