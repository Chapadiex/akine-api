package com.akine_api.application.dto.result;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionSummaryResult(
        UUID id,
        String status,
        String planCode,
        String planNombre,
        String billingCycle,
        String onboardingStep,
        String trackingToken,
        Instant submittedForApprovalAt,
        Instant requestedAt,
        LocalDate startDate,
        LocalDate endDate,
        Instant reviewedAt,
        UUID reviewedByUserId,
        String rejectionReason,
        UUID ownerUserId,
        String ownerFirstName,
        String ownerLastName,
        String ownerEmail,
        UUID empresaId,
        String empresaName,
        String empresaCuit,
        String empresaCity,
        String empresaProvince,
        UUID consultorioBaseId,
        String consultorioBaseName,
        String consultorioBaseAddress,
        String nroConsultorio
) {}
