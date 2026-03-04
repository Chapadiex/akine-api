package com.akine_api.interfaces.api.v1.admin.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ApproveSubscriptionRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
