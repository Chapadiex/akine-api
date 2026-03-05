package com.akine_api.interfaces.api.v1.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestSubscriptionInfoRequest(
        @NotBlank @Size(max = 500) String reason
) {}
