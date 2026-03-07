package com.akine_api.interfaces.api.v1.feriado.dto;

public record FeriadoSyncResponse(
        int year,
        int fetched,
        int created,
        int skippedExisting
) {}
