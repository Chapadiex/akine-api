package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSubscriptionClinicRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 500) String address,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Email @Size(max = 255) String email
) {}
