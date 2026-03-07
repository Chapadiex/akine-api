package com.akine_api.application.dto.result;

public record FeriadoSyncResult(
        int year,
        int fetched,
        int created,
        int skippedExisting
) {}
