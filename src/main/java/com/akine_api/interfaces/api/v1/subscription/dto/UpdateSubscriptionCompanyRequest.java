package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSubscriptionCompanyRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 20) String cuit,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 150) String city,
        @NotBlank @Size(max = 150) String province
) {}
