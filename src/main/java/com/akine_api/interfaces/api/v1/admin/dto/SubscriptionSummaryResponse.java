package com.akine_api.interfaces.api.v1.admin.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionSummaryResponse(
        UUID id,
        String status,
        Instant requestedAt,
        LocalDate startDate,
        LocalDate endDate,
        Instant reviewedAt,
        UUID reviewedByUserId,
        String rejectionReason,
        OwnerInfo owner,
        CompanyInfo company,
        ConsultorioInfo baseConsultorio
) {
    public record OwnerInfo(
            UUID userId,
            String firstName,
            String lastName,
            String email
    ) {}

    public record CompanyInfo(
            UUID id,
            String name,
            String cuit,
            String city,
            String province
    ) {}

    public record ConsultorioInfo(
            UUID id,
            String name,
            String address
    ) {}
}
