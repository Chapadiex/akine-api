package com.akine_api.interfaces.api.v1.subscription.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSubscriptionRequest(
        @NotNull @Valid OwnerData owner,
        @NotNull @Valid CompanyData company,
        @NotNull @Valid BaseConsultorioData baseConsultorio
) {
    public record OwnerData(
            @NotBlank @Size(max = 100) String firstName,
            @NotBlank @Size(max = 100) String lastName,
            @NotBlank @Size(max = 30) String documentoFiscal,
            @NotBlank @Email String email,
            @NotBlank @Size(max = 30) String phone,
            @NotBlank @Size(min = 8, max = 100) String password
    ) {}

    public record CompanyData(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Size(max = 20) String cuit,
            @NotBlank @Size(max = 500) String address,
            @NotBlank @Size(max = 150) String city,
            @NotBlank @Size(max = 150) String province
    ) {}

    public record BaseConsultorioData(
            @NotBlank @Size(max = 255) String name,
            @NotBlank @Size(max = 500) String address,
            @NotBlank @Size(max = 30) String phone,
            @NotBlank @Email @Size(max = 255) String email
    ) {}
}
