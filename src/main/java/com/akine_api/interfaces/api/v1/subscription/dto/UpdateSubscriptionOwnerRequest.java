package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSubscriptionOwnerRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotBlank @Size(max = 30) String documentoFiscal,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 30) String phone,
        @NotBlank @Size(min = 8, max = 100) String password
) {}
