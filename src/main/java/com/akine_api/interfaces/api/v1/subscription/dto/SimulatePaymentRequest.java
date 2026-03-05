package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SimulatePaymentRequest(
        @NotBlank @Size(max = 120) String paymentReference
) {}
