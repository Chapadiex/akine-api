package com.akine_api.interfaces.api.v1.subscription.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record SubscriptionStatusResponse(
        UUID id,
        String status,
        String planCode,
        String billingCycle,
        String onboardingStep,
        String trackingToken,
        Instant submittedForApprovalAt,
        Instant requestedAt,
        LocalDate startDate,
        LocalDate endDate,
        String rejectionReason
) {}
