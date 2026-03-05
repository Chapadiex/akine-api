package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateSubscriptionDraftRequest(
        @NotBlank @Size(max = 40) String planCode,
        @NotBlank @Size(max = 20) String billingCycle,
        @NotBlank @Email String ownerEmail,
        @NotBlank @Size(min = 8, max = 100) String ownerPassword
) {}
